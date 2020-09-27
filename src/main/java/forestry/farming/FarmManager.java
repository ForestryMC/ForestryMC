package forestry.farming;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import forestry.api.core.IErrorLogic;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.farming.*;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.cultivation.IFarmHousingInternal;
import forestry.farming.FarmHelper.FarmWorkStatus;
import forestry.farming.FarmHelper.Stage;
import forestry.farming.multiblock.FarmFertilizerManager;
import forestry.farming.multiblock.FarmHydrationManager;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

public class FarmManager implements INbtReadable, INbtWritable, IStreamable, IExtentCache {
    private final Map<FarmDirection, List<FarmTarget>> targets = new EnumMap<>(FarmDirection.class);
    private final Table<FarmDirection, BlockPos, Integer> lastExtents = HashBasedTable.create();
    private final IFarmHousingInternal housing;
    @Nullable
    private IFarmLogic harvestProvider; // The farm logic which supplied the pending crops.
    private final List<ICrop> pendingCrops = new LinkedList<>();
    private final Stack<ItemStack> pendingProduce = new Stack<>();

    private Stage stage = Stage.CULTIVATE;

    private final Set<IFarmListener> farmListeners = new HashSet<>();

    private final FarmHydrationManager hydrationManager;
    private final FarmFertilizerManager fertilizerManager;
    private final TankManager tankManager;
    private final StandardTank resourceTank;

    // tick updates can come from multiple gearboxes so keep track of them here
    private int farmWorkTicks = 0;

    public FarmManager(IFarmHousingInternal housing) {
        this.housing = housing;
        this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(Fluids.WATER);

        this.tankManager = new TankManager(housing, resourceTank);

        this.hydrationManager = new FarmHydrationManager(housing);
        this.fertilizerManager = new FarmFertilizerManager(housing);
    }

    public FarmHydrationManager getHydrationManager() {
        return hydrationManager;
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    public FarmFertilizerManager getFertilizerManager() {
        return fertilizerManager;
    }

    public StandardTank getResourceTank() {
        return resourceTank;
    }

    public void addListener(IFarmListener listener) {
        farmListeners.add(listener);
    }

    public void removeListener(IFarmListener listener) {
        farmListeners.remove(listener);
    }

    public boolean doWork() {
        farmWorkTicks++;
        if (targets.isEmpty() || farmWorkTicks % 20 == 0) {
            housing.setUpFarmlandTargets(targets);
        }

        IErrorLogic errorLogic = housing.getErrorLogic();

        if (!pendingProduce.isEmpty()) {
            boolean added = housing.getFarmInventory().tryAddPendingProduce(pendingProduce);
            errorLogic.setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);
            return added;
        }

        boolean hasFertilizer = fertilizerManager.maintainFertilizer();
        if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
            return false;
        }

        // Cull queued crops.
        if (!pendingCrops.isEmpty() && harvestProvider != null) {
            ICrop first = pendingCrops.get(0);
            if (cullCrop(first, harvestProvider)) {
                pendingCrops.remove(0);
                return true;
            } else {
                return false;
            }
        }

        // Cultivation and collection
        FarmWorkStatus farmWorkStatus = new FarmWorkStatus();

        World world = housing.getWorldObj();
        List<FarmDirection> farmDirections = Arrays.asList(FarmDirection.values());
        Collections.shuffle(farmDirections, world.rand);
        for (FarmDirection farmSide : farmDirections) {
            IFarmLogic logic = housing.getFarmLogic(farmSide);
            List<FarmTarget> farmTargets = targets.get(farmSide);

            if (stage == Stage.CULTIVATE) {
                for (FarmTarget target : farmTargets) {
                    if (target.getExtent() > 0) {
                        farmWorkStatus.hasFarmland = true;
                        break;
                    }
                }
            }

            if (FarmHelper.isCycleCanceledByListeners(logic, farmSide, farmListeners)) {
                continue;
            }

            // Always try to collect windfall.
            if (collectWindfall(logic)) {
                farmWorkStatus.didWork = true;
            }

            if (stage == Stage.HARVEST) {
                Collection<ICrop> harvested = FarmHelper.harvestTargets(
                        world,
                        housing,
                        farmTargets,
                        logic,
                        farmListeners
                );
                farmWorkStatus.didWork = !harvested.isEmpty();
                if (!harvested.isEmpty()) {
                    pendingCrops.addAll(harvested);
                    pendingCrops.sort(FarmHelper.TopDownICropComparator.INSTANCE);
                    harvestProvider = logic;
                }
            } else if (stage == Stage.CULTIVATE) {
                cultivateTargets(farmWorkStatus, farmTargets, logic, farmSide);
            }

            if (farmWorkStatus.didWork) {
                break;
            }
        }

        if (stage == Stage.CULTIVATE) {
            errorLogic.setCondition(!farmWorkStatus.hasFarmland, EnumErrorCode.NO_FARMLAND);
            errorLogic.setCondition(!farmWorkStatus.hasFertilizer, EnumErrorCode.NO_FERTILIZER);
            errorLogic.setCondition(!farmWorkStatus.hasLiquid, EnumErrorCode.NO_LIQUID_FARM);
        }

        // alternate between cultivation and harvest.
        stage = stage.next();

        return farmWorkStatus.didWork;
    }


    private void cultivateTargets(
            FarmWorkStatus farmWorkStatus,
            List<FarmTarget> farmTargets,
            IFarmLogic logic,
            FarmDirection farmSide
    ) {
        World world = housing.getWorldObj();
        if (farmWorkStatus.hasFarmland && !FarmHelper.isCycleCanceledByListeners(logic, farmSide, farmListeners)) {
            final float hydrationModifier = hydrationManager.getHydrationModifier();
            final int fertilizerConsumption = Math.round(
                    logic.getProperties().getFertilizerConsumption(housing) * Config.fertilizerModifier);
            final int liquidConsumption = logic.getProperties().getWaterConsumption(housing, hydrationModifier);
            final FluidStack liquid = new FluidStack(Fluids.WATER, liquidConsumption);

            for (FarmTarget target : farmTargets) {
                // Check fertilizer and water
                if (!fertilizerManager.hasFertilizer(fertilizerConsumption)) {
                    farmWorkStatus.hasFertilizer = false;
                    continue;
                }

                if (liquid.getAmount() > 0 && !housing.hasLiquid(liquid)) {
                    farmWorkStatus.hasLiquid = false;
                    continue;
                }

                if (FarmHelper.cultivateTarget(world, housing, target, logic, farmListeners)) {
                    // Remove fertilizer and water
                    fertilizerManager.removeFertilizer(fertilizerConsumption);
                    housing.removeLiquid(liquid);

                    farmWorkStatus.didWork = true;
                }
            }
        }
    }

    private boolean collectWindfall(IFarmLogic logic) {
        NonNullList<ItemStack> collected = logic.collect(housing.getWorldObj(), housing);
        if (collected.isEmpty()) {
            return false;
        }

        // Let event handlers know.
        for (IFarmListener listener : farmListeners) {
            listener.hasCollected(collected, logic);
        }

        housing.getFarmInventory().stowProducts(collected, pendingProduce);

        return true;
    }

    private boolean cullCrop(ICrop crop, IFarmLogic provider) {

        // Let event handlers handle the harvest first.
        for (IFarmListener listener : farmListeners) {
            if (listener.beforeCropHarvest(crop)) {
                return true;
            }
        }

        final int fertilizerConsumption = Math.round(
                provider.getProperties().getFertilizerConsumption(housing) * Config.fertilizerModifier);

        IErrorLogic errorLogic = housing.getErrorLogic();

        // Check fertilizer
        boolean hasFertilizer = fertilizerManager.hasFertilizer(fertilizerConsumption);
        if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
            return false;
        }

        // Check water
        float hydrationModifier = hydrationManager.getHydrationModifier();
        int waterConsumption = provider.getProperties().getWaterConsumption(housing, hydrationModifier);
        FluidStack requiredLiquid = new FluidStack(Fluids.WATER, waterConsumption);
        boolean hasLiquid = requiredLiquid.getAmount() == 0 || housing.hasLiquid(requiredLiquid);

        if (errorLogic.setCondition(!hasLiquid, EnumErrorCode.NO_LIQUID_FARM)) {
            return false;
        }

        NonNullList<ItemStack> harvested = crop.harvest();
        if (harvested != null) {
            // Remove fertilizer and water
            fertilizerManager.removeFertilizer(fertilizerConsumption);
            housing.removeLiquid(requiredLiquid);

            // Let event handlers handle the harvest first.
            for (IFarmListener listener : farmListeners) {
                listener.afterCropHarvest(harvested, crop);
            }

            housing.getFarmInventory().stowProducts(harvested, pendingProduce);
        }
        return true;
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        hydrationManager.write(data);
        tankManager.write(data);
        fertilizerManager.write(data);
        return data;
    }

    @Override
    public void read(CompoundNBT data) {
        hydrationManager.read(data);
        tankManager.read(data);
        fertilizerManager.read(data);
    }

    @Override
    public void writeData(PacketBufferForestry data) {
        tankManager.writeData(data);
        hydrationManager.writeData(data);
        fertilizerManager.writeData(data);
    }

    @Override
    public void readData(PacketBufferForestry data) throws IOException {
        tankManager.readData(data);
        hydrationManager.readData(data);
        fertilizerManager.readData(data);
    }

    public void clearTargets() {
        targets.clear();
    }

    public void addPendingProduct(ItemStack stack) {
        this.pendingProduce.add(stack);
    }

    public BlockPos getFarmCorner(FarmDirection direction) {
        List<FarmTarget> targetList = this.targets.get(direction);
        if (targetList.isEmpty()) {
            return housing.getCoords();
        }
        FarmTarget target = targetList.get(0);
        return target.getStart().offset(direction.getFacing().getOpposite());
    }

    @Override
    public int getExtents(FarmDirection direction, BlockPos pos) {
        if (!lastExtents.contains(direction, pos)) {
            lastExtents.put(direction, pos, 0);
            return 0;
        }

        return lastExtents.get(direction, pos);
    }

    @Override
    public void setExtents(FarmDirection direction, BlockPos pos, int extend) {
        lastExtents.put(direction, pos, extend);
    }

    @Override
    public void cleanExtents(FarmDirection direction) {
        lastExtents.row(direction).clear();
    }
}
