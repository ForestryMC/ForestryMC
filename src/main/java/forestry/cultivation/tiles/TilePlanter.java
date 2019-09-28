package forestry.cultivation.tiles;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.ITankManager;
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.VectUtil;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.gui.ContainerPlanter;
import forestry.cultivation.inventory.InventoryPlanter;
import forestry.farming.FarmHelper;
import forestry.farming.FarmHelper.FarmWorkStatus;
import forestry.farming.FarmHelper.Stage;
import forestry.farming.FarmRegistry;
import forestry.farming.FarmTarget;
import forestry.farming.gui.IFarmLedgerDelegate;
import forestry.farming.multiblock.FarmFertilizerManager;
import forestry.farming.multiblock.FarmHydrationManager;

public abstract class TilePlanter extends TilePowered implements IFarmHousing, IClimatised, ILiquidTankTile, IOwnedTile, IStreamableGui {

	private final Map<FarmDirection, List<FarmTarget>> targets = new EnumMap<>(FarmDirection.class);
	private final Stack<ItemStack> pendingProduce = new Stack<>();
	private final List<ICrop> pendingCrops = new LinkedList<>();
	private final String identifier;

	private final FarmHydrationManager hydrationManager;
	private final FarmFertilizerManager fertilizerManager;
	private final InventoryPlanter inventory;
	private final TankManager tankManager;
	private final StandardTank resourceTank;
	private final OwnerHandler ownerHandler = new OwnerHandler();

	private int platformHeight = -1;
	private Stage stage = Stage.CULTIVATE;
	private BlockPlanter.Mode mode;
	private IFarmProperties properties;
	private IFarmLogic logic;
	@Nullable
	private Vec3i offset;
	@Nullable
	private Vec3i area;

	public void setManual(BlockPlanter.Mode mode) {
		this.mode = mode;
		logic = FarmRegistry.getInstance().getProperties(identifier).getLogic(this.mode == BlockPlanter.Mode.MANUAL);
		properties = logic.getProperties();
	}

	protected TilePlanter(TileEntityType type, String identifier) {
		super(type, 150, 1500);
		this.identifier = identifier;
		mode = BlockPlanter.Mode.MANAGED;
		setInternalInventory(inventory = new InventoryPlanter(this));
		this.hydrationManager = new FarmHydrationManager(this);
		this.fertilizerManager = new FarmFertilizerManager();

		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(Fluids.WATER);

		this.tankManager = new TankManager(this, resourceTank);
		setEnergyPerWorkCycle(10);
		setTicksPerWorkCycle(2);
	}

	@Override
	public void setPos(BlockPos posIn) {
		super.setPos(posIn);
		this.platformHeight = posIn.getY() - 2;
	}

	@Override
	public boolean hasWork() {
		return true;
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		hydrationManager.updateServer(world, getCoords());

		if (updateOnInterval(20)) {
			inventory.drainCan(tankManager);
		}
	}

	@Override
	protected boolean workCycle() {
		if (targets.isEmpty() || updateOnInterval(10)) {
			setUpFarmlandTargets();
		}
		IErrorLogic errorLogic = getErrorLogic();

		boolean hasFertilizer = fertilizerManager.maintainFertilizer(inventory);
		if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
			return false;
		}

		if (!pendingProduce.isEmpty()) {
			boolean added = inventory.tryAddPendingProduce(pendingProduce);
			errorLogic.setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);
			return added;
		}

		// Cull queued crops.
		if (!pendingCrops.isEmpty()) {
			ICrop first = pendingCrops.get(0);
			if (cullCrop(first)) {
				pendingCrops.remove(0);
				return true;
			} else {
				return false;
			}
		}

		// Cultivation and collection
		FarmWorkStatus farmWorkStatus = new FarmWorkStatus();

		List<FarmDirection> farmDirections = Arrays.asList(FarmDirection.values());
		Collections.shuffle(farmDirections, world.rand);
		for (FarmDirection farmSide : farmDirections) {
			IFarmLogic logic = getFarmLogic(farmSide);

			// Always try to collect windfall.
			if (collectWindfall()) {
				farmWorkStatus.didWork = true;
			}

			List<FarmTarget> farmTargets = targets.get(farmSide);

			if (stage == Stage.HARVEST) {
				Collection<ICrop> harvested = FarmHelper.harvestTargets(world, farmTargets, logic, Collections.emptySet());
				farmWorkStatus.didWork = !harvested.isEmpty();
				if (!harvested.isEmpty()) {
					pendingCrops.addAll(harvested);
					pendingCrops.sort(FarmHelper.TopDownICropComparator.INSTANCE);
				}
			} else if (stage == Stage.CULTIVATE) {
				farmWorkStatus = cultivateTargets(farmWorkStatus, farmTargets, farmSide);
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

		return false;
	}

	@Override
	public CompoundNBT write(CompoundNBT data) {
		data = super.write(data);
		hydrationManager.write(data);
		tankManager.write(data);
		fertilizerManager.write(data);
		ownerHandler.write(data);
		data.putInt("mode", mode.ordinal());
		return data;
	}

	@Override
	public void read(CompoundNBT data) {
		super.read(data);
		hydrationManager.read(data);
		tankManager.read(data);
		fertilizerManager.read(data);
		ownerHandler.read(data);
		setManual(BlockPlanter.Mode.values()[data.getInt("mode")]);
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		tankManager.writeData(data);
		hydrationManager.writeData(data);
		fertilizerManager.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		tankManager.readData(data);
		hydrationManager.readData(data);
		fertilizerManager.readData(data);
	}

	private void setUpFarmlandTargets() {
		BlockPos targetStart = getCoords();
		BlockPos minPos = pos;
		BlockPos maxPos = pos;
		int size = 1;
		int extend = Config.planterExtend;

		if (Config.ringFarms) {
			int ringSize = Config.ringSize;
			minPos = pos.add(-ringSize, 0, -ringSize);
			maxPos = pos.add(ringSize, 0, ringSize);
			size = 1 + ringSize * 2;
			extend--;
		}

		FarmHelper.createTargets(world, this, targets, targetStart, extend, size, size, minPos, maxPos);
		FarmHelper.setExtents(world, this, targets);
	}

	private boolean cullCrop(ICrop crop) {
		final int fertilizerConsumption = Math.round(properties.getFertilizerConsumption(this) * Config.fertilizerModifier * 2);

		IErrorLogic errorLogic = getErrorLogic();

		// Check fertilizer
		boolean hasFertilizer = fertilizerManager.hasFertilizer(inventory, fertilizerConsumption);
		if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
			return false;
		}

		// Check water
		float hydrationModifier = hydrationManager.getHydrationModifier();
		int waterConsumption = properties.getWaterConsumption(this, hydrationModifier);
		FluidStack requiredLiquid = new FluidStack(Fluids.WATER, waterConsumption);
		boolean hasLiquid = requiredLiquid.getAmount() == 0 || hasLiquid(requiredLiquid);

		if (errorLogic.setCondition(!hasLiquid, EnumErrorCode.NO_LIQUID_FARM)) {
			return false;
		}

		NonNullList<ItemStack> harvested = crop.harvest();
		if (harvested != null) {
			// Remove fertilizer and water
			fertilizerManager.removeFertilizer(inventory, fertilizerConsumption);
			removeLiquid(requiredLiquid);

			inventory.stowHarvest(harvested, pendingProduce);
		}
		return true;
	}

	private boolean collectWindfall() {
		NonNullList<ItemStack> collected = logic.collect(world, this);
		if (collected.isEmpty()) {
			return false;
		}

		for (ItemStack produce : collected) {
			inventory.addProduce(produce);
			pendingProduce.push(produce);
		}

		return true;
	}

	private FarmWorkStatus cultivateTargets(FarmWorkStatus farmWorkStatus, List<FarmTarget> farmTargets, FarmDirection farmSide) {
		boolean hasFarmland = false;
		for (FarmTarget target : farmTargets) {
			if (target.getExtent() > 0) {
				hasFarmland = true;
				farmWorkStatus.hasFarmland = true;
				break;
			}
		}

		if (hasFarmland && !FarmHelper.isCycleCanceledByListeners(logic, farmSide, Collections.emptySet())) {
			final float hydrationModifier = hydrationManager.getHydrationModifier();
			final int fertilizerConsumption = Math.round(properties.getFertilizerConsumption(this) * Config.fertilizerModifier * 2);
			final int liquidConsumption = properties.getWaterConsumption(this, hydrationModifier);
			final FluidStack liquid = new FluidStack(Fluids.WATER, liquidConsumption);

			for (FarmTarget target : farmTargets) {
				// Check fertilizer and water
				if (!fertilizerManager.hasFertilizer(inventory, fertilizerConsumption)) {
					farmWorkStatus.hasFertilizer = false;
					continue;
				}

				if (liquid.getAmount() > 0 && !hasLiquid(liquid)) {
					farmWorkStatus.hasLiquid = false;
					continue;
				}

				if (FarmHelper.cultivateTarget(world, this, target, logic, Collections.emptySet())) {
					// Remove fertilizer and water
					fertilizerManager.removeFertilizer(inventory, fertilizerConsumption);
					removeLiquid(liquid);

					farmWorkStatus.didWork = true;
				}
			}
		}

		return farmWorkStatus;
	}

	@Override
	public BlockPos getCoords() {
		return pos;
	}

	@Override
	public Vec3i getArea() {
		if (area == null) {
			int basisArea = 5;
			if (Config.ringFarms) {
				basisArea = basisArea + 1 + Config.ringSize * 2;
			}
			area = new Vec3i(basisArea + Config.planterExtend, 13, basisArea + Config.planterExtend);
		}
		return area;
	}

	@Override
	public Vec3i getOffset() {
		if (offset == null) {
			Vec3i area = getArea();
			offset = new Vec3i(-area.getX() / 2, -2, -area.getZ() / 2);
		}
		return offset;
	}

	@Override
	public boolean doWork() {
		return false;
	}

	@Override
	public boolean hasLiquid(FluidStack liquid) {
		FluidStack drained = resourceTank.drainInternal(liquid, IFluidHandler.FluidAction.SIMULATE);
		return liquid.isFluidStackIdentical(drained);
	}

	@Override
	public void removeLiquid(FluidStack liquid) {
		resourceTank.drain(liquid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public boolean plantGermling(IFarmable farmable, World world, BlockPos pos, FarmDirection direction) {
		PlayerEntity player = PlayerUtil.getFakePlayer(world, getOwnerHandler().getOwner());
		return player != null && inventory.plantGermling(farmable, player, pos, direction);
	}

	@Override
	public boolean isValidPlatform(World world, BlockPos pos) {
		return pos.getY() == platformHeight;
	}

	@Override
	public boolean isSquare() {
		return true;
	}

	@Override
	public boolean canPlantSoil(boolean manual) {
		return mode == BlockPlanter.Mode.MANAGED;
	}

	@Override
	public IFarmInventory getFarmInventory() {
		return inventory;
	}

	@Override
	public void setFarmLogic(FarmDirection direction, IFarmLogic logic) {
	}

	@Override
	public void resetFarmLogic(FarmDirection direction) {
	}

	@Override
	public IFarmLogic getFarmLogic(FarmDirection direction) {
		return getFarmLogic();
	}

	public IFarmLogic getFarmLogic() {
		return logic;
	}

	@Override
	public int getStoredFertilizerScaled(int scale) {
		return fertilizerManager.getStoredFertilizerScaled(inventory, scale);
	}

	@Override
	public void remove() {
		super.remove();
		targets.clear();
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT data = super.getUpdateTag();
		hydrationManager.write(data);
		tankManager.write(data);
		fertilizerManager.write(data);
		return data;
	}

	protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return VectUtil.scale(farmDirection.getFacing().getDirectionVec(), step).add(pos);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerPlanter(windowId, inv, this);
	}

	public IFarmLedgerDelegate getFarmLedgerDelegate() {
		return hydrationManager;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		BlockPos coords = getCoordinates();
		return world.getBiome(coords).getTemperature(coords);
	}

	@Override
	public float getExactHumidity() {
		BlockPos coords = getCoordinates();
		return world.getBiome(coords).getDownfall();
	}

	@Override
	public ITankManager getTankManager() {
		return tankManager;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(this::getTankManager).cast();
		}
		return super.getCapability(capability, facing);
	}

	protected NonNullList<ItemStack> createList(ItemStack... stacks) {
		return NonNullList.from(ItemStack.EMPTY, stacks);
	}

	public abstract NonNullList<ItemStack> createGermlingStacks();

	public abstract NonNullList<ItemStack> createResourceStacks();

	public abstract NonNullList<ItemStack> createProductionStacks();

}
