/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.farming.multiblock;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.config.Config;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.PlayerUtil;
import forestry.farming.FarmDefinition;
import forestry.farming.FarmHelper;
import forestry.farming.FarmManager;
import forestry.farming.FarmTarget;
import forestry.farming.gui.IFarmLedgerDelegate;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmPlain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FarmController extends RectangularMultiblockControllerBase implements IFarmControllerInternal, ILiquidTankTile {
    private int allowedExtent = 0;

    // active components are stored with a tick offset so they do not all tick together
    private final Map<IFarmComponent.Active, Integer> farmActiveComponents = new HashMap<>();

    private final Map<FarmDirection, IFarmLogic> farmLogics = new EnumMap<>(FarmDirection.class);

    private final InventoryAdapter sockets;
    private final InventoryFarm inventory;
    private final FarmManager manager;

    // the number of work ticks that this farm has had no power
    private int noPowerTime = 0;

    @Nullable
    private Vector3i offset;
    @Nullable
    private Vector3i area;

    public FarmController(World world) {
        super(world, FarmMultiblockSizeLimits.instance);

        this.inventory = new InventoryFarm(this);
        this.manager = new FarmManager(this);
        this.sockets = new InventoryAdapter(1, "sockets");

        refreshFarmLogics();
    }

    @Override
    public IFarmLedgerDelegate getFarmLedgerDelegate() {
        return manager.getHydrationManager();
    }

    @Override
    public IInventoryAdapter getInternalInventory() {
        if (isAssembled()) {
            return inventory;
        } else {
            return FakeInventoryAdapter.instance();
        }
    }

    @Override
    public TankManager getTankManager() {
        return manager.getTankManager();
    }

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockComponent part, CompoundNBT data) {
        this.read(data);
    }

    @Override
    protected void onBlockAdded(IMultiblockComponent newPart) {
        if (newPart instanceof IFarmComponent.Listener) {
            IFarmComponent.Listener listenerPart = (IFarmComponent.Listener) newPart;
            manager.addListener(listenerPart.getFarmListener());
        }

        if (newPart instanceof IFarmComponent.Active) {
            farmActiveComponents.put((IFarmComponent.Active) newPart, world.rand.nextInt(256));
        }
    }

    @Override
    protected void onBlockRemoved(IMultiblockComponent oldPart) {
        if (oldPart instanceof IFarmComponent.Listener) {
            IFarmComponent.Listener listenerPart = (IFarmComponent.Listener) oldPart;
            manager.removeListener(listenerPart.getFarmListener());
        }

        if (oldPart instanceof IFarmComponent.Active) {
            farmActiveComponents.remove(oldPart);
        }
    }

    @Override
    protected void isMachineWhole() throws MultiblockValidationException {
        super.isMachineWhole();

        boolean hasGearbox = false;
        for (IMultiblockComponent part : connectedParts) {
            if (part instanceof TileFarmGearbox) {
                hasGearbox = true;
                break;
            }
        }

        if (!hasGearbox) {
            throw new MultiblockValidationException(
                    new TranslationTextComponent("for.multiblock.farm.error.needGearbox").getString()
            );
        }
    }

    @Override
    protected void onMachineDisassembled() {
        super.onMachineDisassembled();
        manager.clearTargets();
    }

    @Override
    public void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
        if (level == 2 && !(part instanceof TileFarmPlain)) {
            throw new MultiblockValidationException(
                    new TranslationTextComponent("for.multiblock.farm.error.needPlainBand").getString()
            );
        }
    }

    @Override
    public void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
        if (!(part instanceof TileFarmPlain)) {
            throw new MultiblockValidationException(
                    new TranslationTextComponent("for.multiblock.farm.error.needPlainInterior").getString()
            );
        }
    }

    @Override
    public void onAssimilate(IMultiblockControllerInternal assimilated) {

    }

    @Override
    public void onAssimilated(IMultiblockControllerInternal assimilator) {

    }

    @Override
    protected boolean updateServer(int tickCount) {
        manager.getHydrationManager().updateServer();

        if (updateOnInterval(20)) {
            inventory.drainCan(manager.getTankManager());
        }

        boolean hasPower = false;
        for (Map.Entry<IFarmComponent.Active, Integer> entry : farmActiveComponents.entrySet()) {
            IFarmComponent.Active farmComponent = entry.getKey();
            if (farmComponent instanceof TileFarmGearbox) {
                hasPower |= ((TileFarmGearbox) farmComponent).getEnergyManager().getEnergyStored() > 0;
            }

            int tickOffset = entry.getValue();
            farmComponent.updateServer(tickCount + tickOffset);
        }

        if (hasPower) {
            noPowerTime = 0;
            getErrorLogic().setCondition(false, EnumErrorCode.NO_POWER);
        } else {
            if (noPowerTime <= 4) {
                noPowerTime++;
            } else {
                getErrorLogic().setCondition(true, EnumErrorCode.NO_POWER);
            }
        }

        //FIXME: be smarter about the farm needing to save
        return true;
    }

    @Override
    protected void updateClient(int tickCount) {
        for (Map.Entry<IFarmComponent.Active, Integer> entry : farmActiveComponents.entrySet()) {
            IFarmComponent.Active farmComponent = entry.getKey();
            int tickOffset = entry.getValue();
            farmComponent.updateClient(tickCount + tickOffset);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        data = super.write(data);
        sockets.write(data);
        manager.write(data);
        inventory.write(data);
        return data;
    }

    @Override
    public void read(CompoundNBT data) {
        super.read(data);
        sockets.read(data);
        manager.read(data);
        inventory.read(data);

        refreshFarmLogics();
    }

    @Override
    public void formatDescriptionPacket(CompoundNBT data) {
        sockets.write(data);
        manager.write(data);
    }

    @Override
    public void decodeDescriptionPacket(CompoundNBT data) {
        sockets.read(data);
        manager.read(data);

        refreshFarmLogics();
    }

    @Override
    public BlockPos getCoordinates() {
        return getReferenceCoord();
    }

    @Override
    public BlockPos getTopCoord() {
        return getTopCenterCoord();
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        manager.writeData(data);
        sockets.writeData(data);
    }

    @Override
    public void readGuiData(PacketBufferForestry data) throws IOException {
        manager.readData(data);
        sockets.readData(data);

        refreshFarmLogics();
    }

    private void refreshFarmLogics() {
        for (FarmDirection direction : FarmDirection.values()) {
            resetFarmLogic(direction);
        }

        // See whether we have socketed stuff.
        ItemStack chip = sockets.getStackInSlot(0);
        if (!chip.isEmpty()) {
            ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(chip);
            if (chipset != null) {
                chipset.onLoad(this);
            }
        }
    }

    @Override
    public EnumTemperature getTemperature() {
        BlockPos coords = getReferenceCoord();
        return EnumTemperature.getFromBiome(world.getBiome(coords), coords);
    }

    @Override
    public EnumHumidity getHumidity() {
        return EnumHumidity.getFromValue(getExactHumidity());
    }

    @Override
    public float getExactTemperature() {
        BlockPos coords = getReferenceCoord();
        return world.getBiome(coords).getTemperature(coords);
    }

    @Override
    public float getExactHumidity() {
        BlockPos coords = getReferenceCoord();
        return world.getBiome(coords).getDownfall();
    }

    @Override
    public BlockPos getCoords() {
        return getCenterCoord();
    }

    @Override
    public Vector3i getOffset() {
        if (offset == null) {
            Vector3i area = getArea();
            offset = new Vector3i(-area.getX() / 2, -2, -area.getZ() / 2);
        }
        return offset;
    }

    @Override
    public Vector3i getArea() {
        if (area == null) {
            area = new Vector3i(7 + allowedExtent * 2, 13, 7 + allowedExtent * 2);
        }
        return area;
    }

    @Override
    public String getUnlocalizedType() {
        return "for.multiblock.farm.type";
    }

    @Override
    public boolean doWork() {
        return manager.doWork();
    }

    @Override
    public void setUpFarmlandTargets(Map<FarmDirection, List<FarmTarget>> targets) {
        BlockPos targetStart = getCoords();

        BlockPos max = getMaximumCoord();
        BlockPos min = getMinimumCoord();

        int sizeNorthSouth = Math.abs(max.getZ() - min.getZ()) + 1;
        int sizeEastWest = Math.abs(max.getX() - min.getX()) + 1;

        // Set the maximum allowed extent.
        allowedExtent = Math.max(sizeNorthSouth, sizeEastWest) * Config.farmSize + 1;

        FarmHelper.createTargets(
                world,
                this,
                targets,
                targetStart,
                allowedExtent,
                sizeNorthSouth,
                sizeEastWest,
                min,
                max
        );
        FarmHelper.setExtents(world, this, targets);
    }

    @Override
    public int getStoredFertilizerScaled(int scale) {
        return manager.getFertilizerManager().getStoredFertilizerScaled(inventory, scale);
    }

    @Override
    public BlockPos getFarmCorner(FarmDirection direction) {
        return manager.getFarmCorner(direction);
    }

    @Override
    public boolean hasLiquid(FluidStack liquid) {
        FluidStack drained = manager.getResourceTank().drainInternal(liquid, IFluidHandler.FluidAction.SIMULATE);
        return liquid.isFluidStackIdentical(drained);
    }

    @Override
    public void removeLiquid(FluidStack liquid) {
        manager.getResourceTank().drain(liquid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public boolean plantGermling(IFarmable germling, World world, BlockPos pos, FarmDirection direction) {
        PlayerEntity player = PlayerUtil.getFakePlayer(world, getOwnerHandler().getOwner());
        return player != null && inventory.plantGermling(germling, player, pos);
    }

    @Override
    public IFarmInventoryInternal getFarmInventory() {
        return inventory;
    }

    @Override
    public void addPendingProduct(ItemStack stack) {
        manager.addPendingProduct(stack);
    }

    @Override
    public void setFarmLogic(FarmDirection direction, IFarmLogic logic) {
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(logic, "logic must not be null");
        farmLogics.put(direction, logic);
        cleanExtents(direction);
    }

    @Override
    public void resetFarmLogic(FarmDirection direction) {
        setFarmLogic(direction, FarmDefinition.ARBOREAL.getProperties().getLogic(false));
    }

    @Override
    public IFarmLogic getFarmLogic(FarmDirection direction) {
        return farmLogics.get(direction);
    }

    @Override
    public int getSocketCount() {
        return sockets.getSizeInventory();
    }

    @Override
    public ItemStack getSocket(int slot) {
        return sockets.getStackInSlot(slot);
    }

    @Override
    public void setSocket(int slot, ItemStack stack) {
        if (ChipsetManager.circuitRegistry.isChipset(stack) || stack.isEmpty()) {
            // Dispose old chipsets correctly
            if (!sockets.getStackInSlot(slot).isEmpty()) {
                if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
                    ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(sockets.getStackInSlot(slot));
                    if (chipset != null) {
                        chipset.onRemoval(this);
                    }
                }
            }

            sockets.setInventorySlotContents(slot, stack);
            refreshFarmLogics();

            if (!stack.isEmpty()) {
                ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(stack);
                if (chipset != null) {
                    chipset.onInsertion(this);
                }
            }
        }
    }

    @Override
    public ICircuitSocketType getSocketType() {
        return CircuitSocketType.FARM;
    }

    @Override
    public boolean canPlantSoil(boolean manual) {
        return true;
    }

    @Override
    public boolean isValidPlatform(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return FarmHelper.bricks.contains(blockState.getBlock());
    }

    @Override
    public boolean isSquare() {
        return Config.squareFarms;
    }

    @Override
    public int getExtents(FarmDirection direction, BlockPos pos) {
        return manager.getExtents(direction, pos);
    }

    @Override
    public void setExtents(FarmDirection direction, BlockPos pos, int extend) {
        manager.setExtents(direction, pos, extend);
    }

    @Override
    public void cleanExtents(FarmDirection direction) {
        manager.cleanExtents(direction);
    }

    // for debugging
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("logic", farmLogics.toString()).toString();
    }
}
