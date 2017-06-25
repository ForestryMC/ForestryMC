/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.multiblock;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.TopDownBlockPosComparator;
import forestry.core.utils.Translator;
import forestry.farming.FarmHelper;
import forestry.farming.FarmTarget;
import forestry.farming.gui.IFarmLedgerDelegate;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmPlain;

public class FarmController extends RectangularMultiblockControllerBase implements IFarmControllerInternal, ILiquidTankTile {

	private enum Stage {
		CULTIVATE, HARVEST;

		public Stage next() {
			if (this == CULTIVATE) {
				return HARVEST;
			} else {
				return CULTIVATE;
			}
		}
	}

	private static FarmDirection getLayoutDirection(FarmDirection farmSide) {
		switch (farmSide) {
			case NORTH:
				return FarmDirection.WEST;
			case WEST:
				return FarmDirection.SOUTH;
			case SOUTH:
				return FarmDirection.EAST;
			case EAST:
				return FarmDirection.NORTH;
		}
		return null;
	}

	private final Map<FarmDirection, List<FarmTarget>> targets = new EnumMap<>(FarmDirection.class);
	private int allowedExtent = 0;
	@Nullable
	private IFarmLogic harvestProvider; // The farm logic which supplied the pending crops.
	private final List<ICrop> pendingCrops = new LinkedList<>();
	private final Stack<ItemStack> pendingProduce = new Stack<>();

	private Stage stage = Stage.CULTIVATE;

	// active components are stored with a tick offset so they do not all tick together
	private final Map<IFarmComponent.Active, Integer> farmActiveComponents = new HashMap<>();
	private final Set<IFarmListener> farmListeners = new HashSet<>();

	private final Map<FarmDirection, IFarmLogic> farmLogics = new EnumMap<>(FarmDirection.class);

	private final InventoryAdapter sockets;
	private final InventoryFarm inventory;
	private final TankManager tankManager;
	private final StandardTank resourceTank;
	private final FarmHydrationManager hydrationManager;
	private final FarmFertilizerManager fertilizerManager;

	// the number of work ticks that this farm has had no power
	private int noPowerTime = 0;

	// tick updates can come from multiple gearboxes so keep track of them here
	private int farmWorkTicks = 0;

	@Nullable
	private Vec3i offset;
	@Nullable
	private Vec3i area;

	public FarmController(World world) {
		super(world, FarmMultiblockSizeLimits.instance);

		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(FluidRegistry.WATER);

		this.tankManager = new TankManager(this, resourceTank);

		this.inventory = new InventoryFarm(this);
		this.sockets = new InventoryAdapter(1, "sockets");
		this.hydrationManager = new FarmHydrationManager(this);
		this.fertilizerManager = new FarmFertilizerManager();

		refreshFarmLogics();
	}

	@Override
	public IFarmLedgerDelegate getFarmLedgerDelegate() {
		return hydrationManager;
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
		return tankManager;
	}

	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		this.readFromNBT(data);
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
		if (newPart instanceof IFarmComponent.Listener) {
			IFarmComponent.Listener listenerPart = (IFarmComponent.Listener) newPart;
			farmListeners.add(listenerPart.getFarmListener());
		}

		if (newPart instanceof IFarmComponent.Active) {
			farmActiveComponents.put((IFarmComponent.Active) newPart, world.rand.nextInt(256));
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {
		if (oldPart instanceof IFarmComponent.Listener) {
			IFarmComponent.Listener listenerPart = (IFarmComponent.Listener) oldPart;
			farmListeners.remove(listenerPart.getFarmListener());
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
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.farm.error.needGearbox"));
		}
	}

	@Override
	protected void onMachineDisassembled() {
		super.onMachineDisassembled();
		targets.clear();
	}

	@Override
	public void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		if (level == 2 && !(part instanceof TileFarmPlain)) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.farm.error.needPlainBand"));
		}
	}

	@Override
	public void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		if (!(part instanceof TileFarmPlain)) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.farm.error.needPlainInterior"));
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
		hydrationManager.updateServer(world, getTopCenterCoord());

		if (updateOnInterval(20)) {
			inventory.drainCan(tankManager);
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
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		sockets.writeToNBT(data);
		hydrationManager.writeToNBT(data);
		tankManager.writeToNBT(data);
		fertilizerManager.writeToNBT(data);
		inventory.writeToNBT(data);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		sockets.readFromNBT(data);
		hydrationManager.readFromNBT(data);
		tankManager.readFromNBT(data);
		fertilizerManager.readFromNBT(data);
		inventory.readFromNBT(data);

		refreshFarmLogics();
	}

	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		sockets.writeToNBT(data);
		hydrationManager.writeToNBT(data);
		tankManager.writeToNBT(data);
		fertilizerManager.writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		sockets.readFromNBT(data);
		hydrationManager.readFromNBT(data);
		tankManager.readFromNBT(data);
		fertilizerManager.readFromNBT(data);

		refreshFarmLogics();
	}

	@Override
	public BlockPos getCoordinates() {
		return getReferenceCoord();
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		tankManager.writeData(data);
		hydrationManager.writeData(data);
		fertilizerManager.writeData(data);
		sockets.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		tankManager.readData(data);
		hydrationManager.readData(data);
		fertilizerManager.readData(data);
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
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		BlockPos coords = getCoordinates();
		return ClimateUtil.getTemperature(getWorldObj(), coords);
	}

	@Override
	public float getExactHumidity() {
		BlockPos coords = getCoordinates();
		return ClimateUtil.getHumidity(getWorldObj(), coords);
	}

	@Override
	public BlockPos getCoords() {
		return getCenterCoord();
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
	public Vec3i getArea() {
		if (area == null) {
			area = new Vec3i(7 + allowedExtent * 2, 13, 7 + allowedExtent * 2);
		}
		return area;
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.farm.type";
	}

	@Override
	public boolean doWork() {
		farmWorkTicks++;
		if (targets.isEmpty() || farmWorkTicks % 20 == 0) {
			setUpFarmlandTargets();
		}

		IErrorLogic errorLogic = getErrorLogic();

		if (!pendingProduce.isEmpty()) {
			boolean added = inventory.tryAddPendingProduce(pendingProduce);
			errorLogic.setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);
			return added;
		}

		boolean hasFertilizer = fertilizerManager.maintainFertilizer(inventory);
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

		List<FarmDirection> farmDirections = Arrays.asList(FarmDirection.values());
		Collections.shuffle(farmDirections, world.rand);
		for (FarmDirection farmSide : farmDirections) {
			IFarmLogic logic = getFarmLogic(farmSide);

			// Always try to collect windfall.
			if (collectWindfall(logic)) {
				farmWorkStatus.didWork = true;
			}

			List<FarmTarget> farmTargets = targets.get(farmSide);

			if (stage == Stage.HARVEST) {
				Collection<ICrop> harvested = harvestTargets(world, farmTargets, logic, farmListeners);
				farmWorkStatus.didWork = !harvested.isEmpty();
				if (!harvested.isEmpty()) {
					pendingCrops.addAll(harvested);
					pendingCrops.sort(TopDownICropComparator.INSTANCE);
					harvestProvider = logic;
				}
			} else if (stage == Stage.CULTIVATE) {
				farmWorkStatus = cultivateTargets(farmWorkStatus, farmTargets, logic, farmSide);
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

	private void setUpFarmlandTargets() {
		BlockPos targetStart = getCoords();

		BlockPos max = getMaximumCoord();
		BlockPos min = getMinimumCoord();

		int sizeNorthSouth = Math.abs(max.getZ() - min.getZ()) + 1;
		int sizeEastWest = Math.abs(max.getX() - min.getX()) + 1;

		// Set the maximum allowed extent.
		allowedExtent = Math.max(sizeNorthSouth, sizeEastWest) * Config.farmSize + 1;

		createTargets(world, targets, targetStart, allowedExtent, sizeNorthSouth, sizeEastWest, min, max);
		setExtents(world, targets);
	}

	private static void createTargets(World world, Map<FarmDirection, List<FarmTarget>> targets, BlockPos targetStart, final int allowedExtent, final int farmSizeNorthSouth, final int farmSizeEastWest, BlockPos minFarmCoord, BlockPos maxFarmCoord) {
		for (FarmDirection farmSide : FarmDirection.values()) {

			final int farmWidth;
			if (farmSide == FarmDirection.NORTH || farmSide == FarmDirection.SOUTH) {
				farmWidth = farmSizeEastWest;
			} else {
				farmWidth = farmSizeNorthSouth;
			}

			// targets extend sideways in a pinwheel pattern around the farm, so they need to go a little extra distance
			final int targetMaxLimit = allowedExtent + farmWidth;

			FarmDirection layoutDirection = getLayoutDirection(farmSide);

			List<FarmTarget> farmSideTargets = new ArrayList<>();
			targets.put(farmSide, farmSideTargets);

			BlockPos targetLocation = FarmHelper.getFarmMultiblockCorner(targetStart, farmSide, layoutDirection, minFarmCoord, maxFarmCoord);
			BlockPos firstLocation = targetLocation.offset(farmSide.getFacing());
			BlockPos firstGroundPosition = getGroundPosition(world, firstLocation);
			if (firstGroundPosition != null) {
				int groundHeight = firstGroundPosition.getY();

				for (int i = 0; i < allowedExtent; i++) {
					targetLocation = targetLocation.offset(farmSide.getFacing());
					BlockPos groundLocation = new BlockPos(targetLocation.getX(), groundHeight, targetLocation.getZ());

					if (!world.isBlockLoaded(groundLocation)) {
						break;
					}

					IBlockState blockState = world.getBlockState(groundLocation);
					if (!FarmHelper.bricks.contains(blockState.getBlock())) {
						break;
					}

					int targetLimit = targetMaxLimit;
					if (!Config.squareFarms) {
						targetLimit = targetMaxLimit - i - 1;
					}

					FarmTarget target = new FarmTarget(targetLocation, layoutDirection, targetLimit);
					farmSideTargets.add(target);
				}
			}
		}
	}

	@Nullable
	private static BlockPos getGroundPosition(World world, BlockPos targetPosition) {
		if (!world.isBlockLoaded(targetPosition)) {
			return null;
		}

		for (int yOffset = 2; yOffset > -4; yOffset--) {
			BlockPos position = targetPosition.add(0, yOffset, 0);
			IBlockState blockState = world.getBlockState(position);
			if (FarmHelper.bricks.contains(blockState.getBlock())) {
				return position;
			}
		}

		return null;
	}

	private static boolean isCycleCanceledByListeners(IFarmLogic logic, FarmDirection direction, Iterable<IFarmListener> farmListeners) {
		for (IFarmListener listener : farmListeners) {
			if (listener.cancelTask(logic, direction)) {
				return true;
			}
		}
		return false;
	}

	private static void setExtents(World world, Map<FarmDirection, List<FarmTarget>> targets) {
		for (List<FarmTarget> targetsList : targets.values()) {
			if (!targetsList.isEmpty()) {
				BlockPos groundPosition = getGroundPosition(world, targetsList.get(0).getStart());

				for (FarmTarget target : targetsList) {
					target.setExtentAndYOffset(world, groundPosition);
				}
			}
		}
	}

	private static class FarmWorkStatus {
		public boolean didWork = false;
		public boolean hasFarmland = false;
		public boolean hasFertilizer = true;
		public boolean hasLiquid = true;
	}

	private FarmWorkStatus cultivateTargets(FarmWorkStatus farmWorkStatus, List<FarmTarget> farmTargets, IFarmLogic logic, FarmDirection farmSide) {
		boolean hasFarmland = false;
		for (FarmTarget target : farmTargets) {
			if (target.getExtent() > 0) {
				hasFarmland = true;
				farmWorkStatus.hasFarmland = true;
				break;
			}
		}

		if (hasFarmland && !isCycleCanceledByListeners(logic, farmSide, farmListeners)) {
			final float hydrationModifier = hydrationManager.getHydrationModifier();
			final int fertilizerConsumption = logic.getFertilizerConsumption();
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

				if (cultivateTarget(world, this, target, logic, farmListeners)) {
					// Remove fertilizer and water
					fertilizerManager.removeFertilizer(inventory, fertilizerConsumption);
					removeLiquid(liquid);

					farmWorkStatus.didWork = true;
				}
			}
		}

		return farmWorkStatus;
	}

	private static boolean cultivateTarget(World world, IFarmHousing farmHousing, FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		BlockPos targetPosition = target.getStart().add(0, target.getYOffset(), 0);
		if (logic.cultivate(world, farmHousing, targetPosition, target.getDirection(), target.getExtent())) {
			for (IFarmListener listener : farmListeners) {
				listener.hasCultivated(logic, targetPosition, target.getDirection(), target.getExtent());
			}
			return true;
		}

		return false;
	}

	private static Collection<ICrop> harvestTargets(World world, List<FarmTarget> farmTargets, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		for (FarmTarget target : farmTargets) {
			Collection<ICrop> harvested = harvestTarget(world, target, logic, farmListeners);
			if (!harvested.isEmpty()) {
				return harvested;
			}
		}

		return Collections.emptyList();
	}

	private static Collection<ICrop> harvestTarget(World world, FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		BlockPos pos = target.getStart().add(0, target.getYOffset(), 0);
		Collection<ICrop> harvested = logic.harvest(world, pos, target.getDirection(), target.getExtent());
		if (!harvested.isEmpty()) {
			// Let event handlers know.
			for (IFarmListener listener : farmListeners) {
				listener.hasScheduledHarvest(harvested, logic, pos, target.getDirection(), target.getExtent());
			}
		}
		return harvested;
	}

	private boolean collectWindfall(IFarmLogic logic) {
		NonNullList<ItemStack> collected = logic.collect(world, this);
		if (collected.isEmpty()) {
			return false;
		}

		// Let event handlers know.
		for (IFarmListener listener : farmListeners) {
			listener.hasCollected(collected, logic);
		}

		for (ItemStack produce : collected) {
			inventory.addProduce(produce);
			pendingProduce.push(produce);
		}

		return true;
	}

	private boolean cullCrop(ICrop crop, IFarmLogic provider) {

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : farmListeners) {
			if (listener.beforeCropHarvest(crop)) {
				return true;
			}
		}

		final int fertilizerConsumption = provider.getFertilizerConsumption();

		IErrorLogic errorLogic = getErrorLogic();

		// Check fertilizer
		Boolean hasFertilizer = fertilizerManager.hasFertilizer(inventory, fertilizerConsumption);
		if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
			return false;
		}

		// Check water
		float hydrationModifier = hydrationManager.getHydrationModifier();
		int waterConsumption = provider.getWaterConsumption(hydrationModifier);
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

			// Let event handlers handle the harvest first.
			for (IFarmListener listener : farmListeners) {
				listener.afterCropHarvest(harvested, crop);
			}

			inventory.stowHarvest(harvested, pendingProduce);
		}
		return true;
	}

	@Override
	public int getStoredFertilizerScaled(int scale) {
		return fertilizerManager.getStoredFertilizerScaled(inventory, scale);
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
		EntityPlayer player = PlayerUtil.getPlayer(world, getOwnerHandler().getOwner());
		return player != null && inventory.plantGermling(germling, player, pos);
	}

	@Override
	public IFarmInventory getFarmInventory() {
		return inventory;
	}

	@Override
	public void setFarmLogic(FarmDirection direction, IFarmLogic logic) {
		Preconditions.checkNotNull(direction);
		Preconditions.checkNotNull(logic, "logic must not be null");
		farmLogics.put(direction, logic);
	}

	@Override
	public void resetFarmLogic(FarmDirection direction) {
		setFarmLogic(direction, new FarmLogicArboreal());
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
		if (ChipsetManager.circuitRegistry.isChipset(stack)) {
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

	// for debugging
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("stage", stage).add("logic", farmLogics.toString()).toString();
	}

	private static class TopDownICropComparator implements Comparator<ICrop> {
		public static final TopDownICropComparator INSTANCE = new TopDownICropComparator();

		private TopDownICropComparator() {

		}

		@Override
		public int compare(ICrop o1, ICrop o2) {
			return TopDownBlockPosComparator.INSTANCE.compare(o1.getPosition(), o2.getPosition());
		}
	}
}
