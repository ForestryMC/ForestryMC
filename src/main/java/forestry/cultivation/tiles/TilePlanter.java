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

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.IClimateListener;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmLogic;
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
import forestry.cultivation.gui.ContainerPlanter;
import forestry.cultivation.gui.GuiPlanter;
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
	private final IClimateListener listener;

	private int platformHeight = -1;
	private Stage stage = Stage.CULTIVATE;
	private boolean isManual;
	private IFarmLogic logic;
	@Nullable
	private Vec3i offset;
	@Nullable
	private Vec3i area;

	public void setManual(boolean manual) {
		isManual = manual;
		logic = FarmRegistry.getInstance().getProperties(identifier).getLogic(manual);
	}

	protected TilePlanter(String identifier) {
		super(150, 1500);
		this.identifier = identifier;
		setManual(false);
		setInternalInventory(inventory = new InventoryPlanter(this));
		this.hydrationManager = new FarmHydrationManager(this);
		this.fertilizerManager = new FarmFertilizerManager();

		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(FluidRegistry.WATER);

		this.tankManager = new TankManager(this, resourceTank);
		this.listener = ClimateManager.climateFactory.createListener(this);
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
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		hydrationManager.writeToNBT(data);
		tankManager.writeToNBT(data);
		fertilizerManager.writeToNBT(data);
		ownerHandler.writeToNBT(data);
		data.setBoolean("isManual", isManual);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		hydrationManager.readFromNBT(data);
		tankManager.readFromNBT(data);
		fertilizerManager.readFromNBT(data);
		ownerHandler.readFromNBT(data);
		setManual(data.getBoolean("isManual"));
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
		final int fertilizerConsumption = Math.round(logic.getFertilizerConsumption() * Config.fertilizerModifier * 2);

		IErrorLogic errorLogic = getErrorLogic();

		// Check fertilizer
		Boolean hasFertilizer = fertilizerManager.hasFertilizer(inventory, fertilizerConsumption);
		if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
			return false;
		}

		// Check water
		float hydrationModifier = hydrationManager.getHydrationModifier();
		int waterConsumption = logic.getWaterConsumption(hydrationModifier);
		FluidStack requiredLiquid = new FluidStack(FluidRegistry.WATER, waterConsumption);
		boolean hasLiquid = requiredLiquid.amount == 0 || hasLiquid(requiredLiquid);

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
			final int fertilizerConsumption = Math.round(logic.getFertilizerConsumption() * Config.fertilizerModifier * 2);
			final int liquidConsumption = logic.getWaterConsumption(hydrationModifier);
			final FluidStack liquid = new FluidStack(FluidRegistry.WATER, liquidConsumption);

			for (FarmTarget target : farmTargets) {
				// Check fertilizer and water
				if (!fertilizerManager.hasFertilizer(inventory, fertilizerConsumption)) {
					farmWorkStatus.hasFertilizer = false;
					continue;
				}

				if (liquid.amount > 0 && !hasLiquid(liquid)) {
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
		FluidStack drained = resourceTank.drainInternal(liquid, false);
		return liquid.isFluidStackIdentical(drained);
	}

	@Override
	public void removeLiquid(FluidStack liquid) {
		resourceTank.drain(liquid.amount, true);
	}

	@Override
	public boolean plantGermling(IFarmable germling, World world, BlockPos pos) {
		EntityPlayer player = PlayerUtil.getFakePlayer(world, getOwnerHandler().getOwner());
		return player != null && inventory.plantGermling(germling, player, pos);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public boolean plantGermling(IFarmable farmable, World world, BlockPos pos, FarmDirection direction) {
		EntityPlayer player = PlayerUtil.getFakePlayer(world, getOwnerHandler().getOwner());
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
		return !isManual;
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
	public void invalidate() {
		super.invalidate();
		targets.clear();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound data = super.getUpdateTag();
		hydrationManager.writeToNBT(data);
		tankManager.writeToNBT(data);
		fertilizerManager.writeToNBT(data);
		return data;
	}

	protected final BlockPos translateWithOffset(BlockPos pos, FarmDirection farmDirection, int step) {
		return VectUtil.scale(farmDirection.getFacing().getDirectionVec(), step).add(pos);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiPlanter(this, player.inventory);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerPlanter(this, player.inventory);
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
		return listener.getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return listener.getExactHumidity();
	}

	@Override
	public ITankManager getTankManager() {
		return tankManager;
	}


	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTankManager());
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
