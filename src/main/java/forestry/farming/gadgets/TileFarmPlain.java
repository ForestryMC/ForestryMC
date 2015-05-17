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
package forestry.farming.gadgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.core.EnumErrorCode;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.ISocketable;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.proxy.Proxies;
import forestry.core.utils.DelayTimer;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.farming.FarmHelper;
import forestry.farming.FarmTarget;
import forestry.farming.logic.FarmLogic;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.plugins.PluginFarming;

public class TileFarmPlain extends TileFarm implements IFarmHousing, ISocketable, IClimatised, IHintSource {

	private enum Stage {
		CULTIVATE, HARVEST;

		public Stage next() {
			Stage[] values = values();
			int ordinal = (this.ordinal() + 1) % values.length;
			return values[ordinal];
		}
	}

	public static final ForgeDirection[] CARDINAL_DIRECTIONS = {ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};

	public static ForgeDirection getLayoutDirection(ForgeDirection farmSide) {
		switch (farmSide) {
			case NORTH:
				return ForgeDirection.WEST;
			case WEST:
				return ForgeDirection.SOUTH;
			case SOUTH:
				return ForgeDirection.EAST;
			case EAST:
				return ForgeDirection.NORTH;
			default:
				return ForgeDirection.UNKNOWN;
		}
	}

	public static final int SLOT_RESOURCES_1 = 0;
	public static final int SLOT_RESOURCES_COUNT = 6;
	public static final int SLOT_GERMLINGS_1 = 6;
	public static final int SLOT_GERMLINGS_COUNT = 6;
	public static final int SLOT_PRODUCTION_1 = 12;
	public static final int SLOT_PRODUCTION_COUNT = 8;

	public static final int SLOT_FERTILIZER = 20;
	public static final int SLOT_FERTILIZER_COUNT = 1;
	public static final int SLOT_CAN = 21;
	public static final int SLOT_CAN_COUNT = 1;

	public static final int SLOT_COUNT = SLOT_RESOURCES_COUNT + SLOT_GERMLINGS_COUNT + SLOT_PRODUCTION_COUNT + SLOT_FERTILIZER_COUNT + SLOT_CAN_COUNT;

	private static final int DELAY_HYDRATION = 100;
	private static final float RAINFALL_MODIFIER_CAP = 15f;
	private static final int BUFFER_FERTILIZER = 200;

	private IFarmLogic[] farmLogics = new IFarmLogic[4];

	private TreeMap<ForgeDirection, List<FarmTarget>> targets;
	private int allowedExtent = 0;
	private final DelayTimer checkTimer = new DelayTimer();

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");

	private TankManager tankManager;

	private IFarmLogic harvestProvider; // The farm logic which supplied the pending crops.
	private final Stack<ICrop> pendingCrops = new Stack<ICrop>();
	private final Stack<ItemStack> pendingProduce = new Stack<ItemStack>();
	private int storedFertilizer;
	private int fertilizerValue = 2000;

	private Stage stage = Stage.CULTIVATE;

	private BiomeGenBase biome;

	private int hydrationDelay = 0;
	private int ticksSinceRainfall = 0;

	public TileFarmPlain() {
		fertilizerValue = GameMode.getGameMode().getIntegerSetting("farms.fertilizer.value");
	}

	@Override
	public void validate() {
		super.validate();
		setBiomeInformation();
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		storedFertilizer = nbttagcompound.getInteger("StoredFertilizer");
		hydrationDelay = nbttagcompound.getInteger("HydrationDelay");
		ticksSinceRainfall = nbttagcompound.getInteger("TicksSinceRainfall");
		sockets.readFromNBT(nbttagcompound);

		refreshFarmLogics();

		if (tankManager != null) {
			tankManager.readTanksFromNBT(nbttagcompound);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		sockets.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("StoredFertilizer", storedFertilizer);
		nbttagcompound.setInteger("HydrationDelay", hydrationDelay);
		nbttagcompound.setInteger("TicksSinceRainfall", ticksSinceRainfall);

		if (tankManager != null) {
			tankManager.writeTanksToNBT(nbttagcompound);
		}
	}

	/* UPDATING */
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (!isMaster()) {
			return;
		}

		if (worldObj.isRaining()) {
			if (hydrationDelay > 0) {
				hydrationDelay--;
			} else {
				ticksSinceRainfall = 0;
			}
		} else {
			hydrationDelay = DELAY_HYDRATION;
			if (ticksSinceRainfall < Integer.MAX_VALUE) {
				ticksSinceRainfall++;
			}
		}

		if (!updateOnInterval(20)) {
			return;
		}

		// Check if we have suitable items waiting in the item slot
		if (getInternalInventory().getStackInSlot(SLOT_CAN) != null) {
			FluidHelper.drainContainers(tankManager, getInternalInventory(), SLOT_CAN);
		}

	}

	private void setBiomeInformation() {
		this.biome = Utils.getBiomeAt(worldObj, xCoord, zCoord);
	}

	private static TreeMap<ForgeDirection, List<FarmTarget>> createTargets(World world, Vect targetStart, final int allowedExtent, final int farmSizeNorthSouth, final int farmSizeEastWest) {

		TreeMap<ForgeDirection, List<FarmTarget>> targets = new TreeMap<ForgeDirection, List<FarmTarget>>();

		for (ForgeDirection farmSide : CARDINAL_DIRECTIONS) {

			int farmSize;
			if (farmSide == ForgeDirection.NORTH || farmSide == ForgeDirection.SOUTH) {
				farmSize = farmSizeNorthSouth;
			} else {
				farmSize = farmSizeEastWest;
			}

			// targets extend sideways in a pinwheel pattern around the farm, so they need to go a little extra distance
			final int targetMaxLimit = allowedExtent + farmSize;

			ForgeDirection layoutDirection = getLayoutDirection(farmSide);

			Vect targetLocation = FarmHelper.getFarmMultiblockCorner(world, targetStart, farmSide, layoutDirection.getOpposite());
			Vect firstLocation = targetLocation.add(farmSide);
			Vect firstGroundPosition = getGroundPosition(world, firstLocation);
			if (firstGroundPosition == null) {
				break;
			}
			int groundHeight = firstGroundPosition.getY();

			List<FarmTarget> farmSideTargets = new ArrayList<FarmTarget>();
			for (int i = 0; i < allowedExtent; i++) {
				targetLocation = targetLocation.add(farmSide);
				Vect groundLocation = new Vect(targetLocation.getX(), groundHeight, targetLocation.getZ());

				int targetLimit = targetMaxLimit;
				if (!Config.squareFarms) {
					targetLimit = targetMaxLimit - i - 1;
				}

				Block platform = VectUtil.getBlock(world, groundLocation);
				Vect soilPosition = new Vect(groundLocation.x, groundLocation.y + 1, groundLocation.z);
				if (!StructureLogicFarm.bricks.contains(platform) || !FarmLogic.canBreakSoil(world, soilPosition)) {
					break;
				}

				FarmTarget target = new FarmTarget(targetLocation, layoutDirection, targetLimit);
				farmSideTargets.add(target);
			}

			targets.put(farmSide, farmSideTargets);
		}

		return targets;
	}

	private void setExtents() {
		for (List<FarmTarget> targetsList : targets.values()) {
			if (!targetsList.isEmpty()) {
				Vect groundPosition = getGroundPosition(worldObj, targetsList.get(0).getStart());

				for (FarmTarget target : targetsList) {
					target.setExtentAndYOffset(worldObj, groundPosition);
				}
			}
		}
	}

	private static Vect getGroundPosition(World world, Vect targetPosition) {
		for (int yOffset = 2; yOffset > -4; yOffset--) {
			Vect position = targetPosition.add(0, yOffset, 0);
			Block ground = VectUtil.getBlock(world, position);
			if (StructureLogicFarm.bricks.contains(ground)) {
				return position;
			}
		}
		return null;
	}

	protected void createInventory() {
		setInternalInventory(new FarmPlainInventoryAdapter(this));
		FilteredTank liquidTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, FluidRegistry.WATER);
		tankManager = new TankManager(liquidTank);
	}

	@Override
	public boolean doWork() {
		// System.out.println("Starting doWork()");
		if (targets == null || checkTimer.delayPassed(worldObj, 400)) {
			Vect targetStart = new Vect(xCoord, yCoord, zCoord);

			int sizeNorthSouth = FarmHelper.getFarmSizeNorthSouth(worldObj, targetStart);
			int sizeEastWest = FarmHelper.getFarmSizeEastWest(worldObj, targetStart);

			// Set the maximum allowed extent.
			allowedExtent = Math.max(sizeNorthSouth, sizeEastWest) * 3;

			targets = createTargets(worldObj, targetStart, allowedExtent, sizeNorthSouth, sizeEastWest);
			setExtents();
		}

		// System.out.println("Targets set");
		if (tryAddPending()) {
			return true;
		}

		// System.out.println("Nothing pending added.");
		// Abort if we still have produce waiting.
		if (setErrorCondition(!pendingProduce.isEmpty(), EnumErrorCode.NOSPACE)) {
			return false;
		}

		// System.out.println("Product queue empty.");
		if (storedFertilizer <= BUFFER_FERTILIZER) {
			replenishFertilizer();
			if (setErrorCondition(storedFertilizer <= 0, EnumErrorCode.NOFERTILIZER)) {
				return false;
			}
		}

		// Cull queued crops.
		if (!pendingCrops.isEmpty()) {
			if (cullCrop(pendingCrops.peek(), harvestProvider)) {
				pendingCrops.pop();
				return true;
			} else {
				return false;
			}
		}

		// Cultivation and collection
		FarmWorkStatus farmWorkStatus = new FarmWorkStatus();

		for (Map.Entry<ForgeDirection, List<FarmTarget>> entry : targets.entrySet()) {

			ForgeDirection farmSide = entry.getKey();
			IFarmLogic logic = getFarmLogic(farmSide);
			if (logic == null) {
				continue;
			}

			// Allow listeners to cancel this cycle.
			if (isCycleCanceledByListeners(logic, farmSide)) {
				continue;
			}

			// Always try to collect windfall first.
			if (collectWindfall(logic)) {
				farmWorkStatus.didWork = true;
			} else {
				List<FarmTarget> farmTargets = entry.getValue();

				if (stage == Stage.HARVEST) {
					farmWorkStatus.didWork = harvestTargets(farmTargets, logic);
				} else {
					farmWorkStatus = cultivateTargets(farmWorkStatus, farmTargets, logic);
				}
			}

			if (farmWorkStatus.didWork) {
				break;
			}
		}

		if (!farmWorkStatus.didWork) {
			if (stage == Stage.CULTIVATE) {
				setErrorCondition(!farmWorkStatus.hasFarmland, EnumErrorCode.NOFARMLAND);
				setErrorCondition(!farmWorkStatus.hasFertilizer, EnumErrorCode.NOFERTILIZER);
				setErrorCondition(!farmWorkStatus.hasLiquid, EnumErrorCode.NOLIQUID);
			}
		}

		// Farms alternate between cultivation and harvest.
		stage = stage.next();

		return farmWorkStatus.didWork;
	}

	private IFarmLogic getFarmLogic(ForgeDirection direction) {
		int logicOrdinal = direction.ordinal() - 2;
		if (farmLogics.length <= logicOrdinal) {
			return null;
		}

		return farmLogics[logicOrdinal];
	}

	private boolean isCycleCanceledByListeners(IFarmLogic logic, ForgeDirection direction) {

		for (IFarmListener listener : eventHandlers) {
			if (listener.cancelTask(logic, direction)) {
				return true;
			}
		}

		return false;
	}

	private static class FarmWorkStatus {
		public boolean didWork = false;
		public boolean hasFarmland = false;
		public boolean hasFertilizer = false;
		public boolean hasLiquid = false;
	}

	private FarmWorkStatus cultivateTargets(FarmWorkStatus farmWorkStatus, List<FarmTarget> farmTargets, IFarmLogic logic) {

		for (FarmTarget target : farmTargets) {

			if (target.getExtent() <= 0) {
				break;
			} else {
				farmWorkStatus.hasFarmland = true;
			}

			// Check fertilizer and water
			if (!hasFertilizer(logic.getFertilizerConsumption())) {
				continue;
			} else {
				farmWorkStatus.hasFertilizer = true;
			}

			int liquidAmount = logic.getWaterConsumption(getHydrationModifier());
			FluidStack liquid = Fluids.WATER.getFluid(liquidAmount);
			if (liquid.amount > 0 && !hasLiquid(liquid)) {
				continue;
			} else {
				farmWorkStatus.hasLiquid = true;
			}

			if (cultivateTarget(target, logic)) {
				// Remove fertilizer and water
				removeFertilizer(logic.getFertilizerConsumption());
				removeLiquid(liquid);

				farmWorkStatus.didWork = true;
			}
		}

		return farmWorkStatus;
	}

	private boolean cultivateTarget(FarmTarget target, IFarmLogic logic) {
		Vect targetPosition = target.getStart().add(0, target.getYOffset(), 0);
		if (logic.cultivate(targetPosition.x, targetPosition.y, targetPosition.z, target.getDirection(), target.getExtent())) {
			for (IFarmListener listener : eventHandlers) {
				listener.hasCultivated(logic, targetPosition.x, targetPosition.y, targetPosition.z, target.getDirection(), target.getExtent());
			}
			return true;
		}

		return false;
	}

	private boolean harvestTargets(List<FarmTarget> farmTargets, IFarmLogic logic) {
		for (FarmTarget target : farmTargets) {
			if (harvestTarget(target, logic)) {
				return true;
			}
		}

		return false;
	}

	private boolean harvestTarget(FarmTarget target, IFarmLogic logic) {
		Collection<ICrop> next = logic.harvest(target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, target.getDirection(), target.getExtent());
		if (next == null || next.size() <= 0) {
			return false;
		}

		// Let event handlers know.
		for (IFarmListener listener : eventHandlers) {
			listener.hasScheduledHarvest(next, logic, target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, target.getDirection(), target.getExtent());
		}

		pendingCrops.addAll(next);
		harvestProvider = logic;
		return true;
	}

	private boolean collectWindfall(IFarmLogic logic) {

		Collection<ItemStack> collected = logic.collect();
		if (collected == null || collected.size() <= 0) {
			return false;
		}

		// Let event handlers know.
		for (IFarmListener listener : eventHandlers) {
			listener.hasCollected(collected, logic);
		}

		for (ItemStack produce : collected) {
			addProduceToInventory(produce);
			pendingProduce.push(produce);
		}

		return true;
	}

	private void addProduceToInventory(ItemStack produce) {
		IInventoryAdapter inventory = getInternalInventory();
		for (IFarmLogic logic : getFarmLogics()) {
			// Germlings try to go into germling slots first.
			if (logic.isAcceptedGermling(produce)) {
				produce.stackSize -= InvTools.addStack(inventory, produce, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT, true);
			}
			if (produce.stackSize <= 0) {
				return;
			}

			if (logic.isAcceptedResource(produce)) {
				produce.stackSize -= InvTools.addStack(inventory, produce, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT, true);
			}
			if (produce.stackSize <= 0) {
				return;
			}
		}

		produce.stackSize -= InvTools.addStack(inventory, produce, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT, true);
	}

	private boolean cullCrop(ICrop crop, IFarmLogic provider) {

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : eventHandlers) {
			if (listener.beforeCropHarvest(crop)) {
				return true;
			}
		}

		// Check fertilizer
		Boolean hasFertilizer = hasFertilizer(provider.getFertilizerConsumption());
		if (setErrorCondition(!hasFertilizer, EnumErrorCode.NOFERTILIZER)) {
			return false;
		}

		// Check water
		FluidStack requiredLiquid = Fluids.WATER.getFluid(provider.getWaterConsumption(getHydrationModifier()));
		boolean hasLiquid = requiredLiquid.amount == 0 || hasLiquid(requiredLiquid);

		if (setErrorCondition(!hasLiquid, EnumErrorCode.NOLIQUID)) {
			return false;
		}

		Collection<ItemStack> harvested = crop.harvest();
		if (harvested == null) {
			Proxies.log.fine("Failed to harvest crop: " + crop.toString());
			return true;
		}

		// Remove fertilizer and water
		removeFertilizer(provider.getFertilizerConsumption());
		removeLiquid(requiredLiquid);

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : eventHandlers) {
			listener.afterCropHarvest(harvested, crop);
		}

		IInventoryAdapter inventory = getInternalInventory();

		// Stow harvest.
		for (ItemStack harvest : harvested) {

			// Special case germlings
			for (IFarmLogic logic : farmLogics) {
				if (logic.isAcceptedGermling(harvest)) {
					harvest.stackSize -= InvTools.addStack(inventory, harvest, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT, true);
					break;
				}
			}

			// Handle the rest
			if (harvest.stackSize <= 0) {
				continue;
			}

			harvest.stackSize -= InvTools.addStack(inventory, harvest, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT, true);
			if (harvest.stackSize <= 0) {
				continue;
			}

			pendingProduce.push(harvest);
		}

		return true;
	}

	private boolean tryAddPending() {
		if (pendingProduce.isEmpty()) {
			return false;
		}

		ItemStack next = pendingProduce.peek();
		boolean added = InvTools.tryAddStack(getInternalInventory(), next, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT, true, true);

		if (added) {
			pendingProduce.pop();
		}

		setErrorCondition(!added, EnumErrorCode.NOSPACE);

		return added;
	}

	/* FERTILIZER HANDLING */
	private void replenishFertilizer() {
		if (fertilizerValue < 0) {
			storedFertilizer += 2000;
			return;
		}

		IInventoryAdapter inventory = getInternalInventory();
		ItemStack fertilizer = inventory.getStackInSlot(SLOT_FERTILIZER);
		if (fertilizer == null || fertilizer.stackSize <= 0) {
			return;
		}

		if (!acceptsAsFertilizer(fertilizer)) {
			return;
		}

		inventory.decrStackSize(SLOT_FERTILIZER, 1);
		storedFertilizer += fertilizerValue;
	}

	private boolean hasFertilizer(int amount) {
		if (fertilizerValue < 0) {
			return true;
		}

		return storedFertilizer >= amount;
	}

	private void removeFertilizer(int amount) {

		if (fertilizerValue < 0) {
			return;
		}

		storedFertilizer -= amount;
		if (storedFertilizer < 0) {
			storedFertilizer = 0;
		}
	}

	public int getStoredFertilizerScaled(int scale) {
		if (storedFertilizer == 0) {
			return 0;
		}

		return (storedFertilizer * scale) / (fertilizerValue + BUFFER_FERTILIZER);
	}

	/* STRUCTURE HANDLING */
	@Override
	public void makeMaster() {
		super.makeMaster();
		refreshFarmLogics();
		createInventory();
	}

	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public void setFarmLogic(ForgeDirection direction, IFarmLogic logic) {
		farmLogics[direction.ordinal() - 2] = logic;
	}

	@Override
	public void resetFarmLogic(ForgeDirection direction) {
		setFarmLogic(direction, new FarmLogicArboreal(this));
	}

	public IFarmLogic[] getFarmLogics() {
		return farmLogics;
	}

	private void refreshFarmLogics() {

		farmLogics = new IFarmLogic[]{new FarmLogicArboreal(this), new FarmLogicArboreal(this), new FarmLogicArboreal(this), new FarmLogicArboreal(this)};

		// See whether we have socketed stuff.
		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
	}

	/* ISOCKETABLE */
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

		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack)) {
			return;
		}

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null) {
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		sockets.setInventorySlotContents(slot, stack);
		refreshFarmLogics();

		if (stack == null) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

	/* IFARMHOUSING */
	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public boolean hasResources(ItemStack[] resources) {
		return InvTools.contains(getInternalInventory(), resources, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT);
	}

	public boolean hasResourcesAmount(int amount) {
		return InvTools.containsAmount(getInternalInventory(), amount, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT);
	}

	public boolean hasGermlingsPercent(float percent) {
		return InvTools.containsPercent(getInternalInventory(), percent, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT);
	}

	public boolean hasFertilizerPercent(float percent) {
		return InvTools.containsPercent(getInternalInventory(), percent, SLOT_FERTILIZER, 1);
	}

	@Override
	public void removeResources(ItemStack[] resources) {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, getOwnerProfile());
		InvTools.removeSets(getInternalInventory(), 1, resources, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT, player, false, true, true);
	}

	@Override
	public boolean hasLiquid(FluidStack liquid) {
		return tankManager.get(0).getFluidAmount() >= liquid.amount;
	}

	@Override
	public void removeLiquid(FluidStack liquid) {
		tankManager.drain(liquid, true);
	}

	public boolean hasGermlings(ItemStack[] germlings) {
		return InvTools.contains(getInternalInventory(), germlings, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT);
	}

	@Override
	public boolean plantGermling(IFarmable germling, World world, int x, int y, int z) {
		IInventoryAdapter inventory = getInternalInventory();
		for (int i = SLOT_GERMLINGS_1; i < SLOT_GERMLINGS_1 + SLOT_GERMLINGS_COUNT; i++) {
			if (inventory.getStackInSlot(i) == null) {
				continue;
			}
			if (!germling.isGermling(inventory.getStackInSlot(i))) {
				continue;
			}

			EntityPlayer player = Proxies.common.getPlayer(world, getOwnerProfile());
			if (!germling.plantSaplingAt(player, inventory.getStackInSlot(i), world, x, y, z)) {
				continue;
			}

			inventory.decrStackSize(i, 1);
			return true;
		}
		return false;
	}

	/* IFARMCOMPONENT */
	private final Set<IFarmListener> eventHandlers = new LinkedHashSet<IFarmListener>();

	@Override
	public boolean hasFunction() {
		return false;
	}

	@Override
	public void registerListener(IFarmListener listener) {
		eventHandlers.add(listener);
	}

	@Override
	public void removeListener(IFarmListener listener) {
		eventHandlers.remove(listener);
	}

	@Override
	public boolean acceptsAsGermling(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		if (farmLogics == null) {
			return false;
		}

		for (IFarmLogic logic : farmLogics) {
			if (logic.isAcceptedGermling(itemstack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean acceptsAsResource(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		if (farmLogics == null) {
			return false;
		}

		for (IFarmLogic logic : farmLogics) {
			if (logic.isAcceptedResource(itemstack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean acceptsAsFertilizer(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}

		return StackUtils.isIdenticalItem(PluginFarming.farmFertilizer, itemstack);
	}

	private int[] coords;
	private int[] offset;
	private int[] area;

	@Override
	public int[] getCoords() {
		if (coords == null) {
			coords = new int[]{xCoord, yCoord, zCoord};
		}
		return coords;
	}

	@Override
	public int[] getOffset() {
		if (offset == null) {
			offset = new int[]{-getArea()[0] / 2, -2, -getArea()[2] / 2};
		}
		return offset;
	}

	@Override
	public int[] getArea() {
		if (area == null) {
			area = new int[]{7 + (allowedExtent * 2), 13, 7 + (allowedExtent * 2)};
		}
		return area;
	}

	/* NETWORK GUI */
	public void getGUINetworkData(int i, int j) {
		if (tankManager != null) {
			i -= tankManager.maxMessageId() + 1;
		}
		switch (i) {
			case 0:
				storedFertilizer = j;
				break;
			case 5:
				ticksSinceRainfall = j;
				break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, storedFertilizer);
		iCrafting.sendProgressBarUpdate(container, i + 5, ticksSinceRainfall);
	}

	/* ICLIMATISED */
	public float getHydrationModifier() {
		return getHydrationTempModifier() * getHydrationHumidModifier() * getHydrationRainfallModifier();
	}

	public float getHydrationTempModifier() {
		float temperature = getExactTemperature();
		return temperature > 0.8f ? temperature : 0.8f;
	}

	public float getHydrationHumidModifier() {
		float mod = 1 / getExactHumidity();
		return mod < 2.0f ? mod : 2.0f;
	}

	public float getHydrationRainfallModifier() {
		float mod = (float) ticksSinceRainfall / 24000;
		return mod > 0.5f ? mod < RAINFALL_MODIFIER_CAP ? mod : RAINFALL_MODIFIER_CAP : 0.5f;
	}

	public double getDrought() {
		return Math.round(((double) ticksSinceRainfall / 24000) * 10) / 10.;
	}

	@Override
	public boolean isClimatized() {
		return true;
	}

	@Override
	public EnumTemperature getTemperature() {
		if (BiomeHelper.isBiomeHellish(biome)) {
			return EnumTemperature.HELLISH;
		}

		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return biome.temperature;
	}

	@Override
	public float getExactHumidity() {
		return biome.rainfall;
	}

	/* IHINTSOURCE */
	@Override
	public boolean hasHints() {
		return Config.hints.get("farm").length > 0;
	}

	@Override
	public String[] getHints() {
		return Config.hints.get("farm");
	}

	/* IOWNABLE */
	@Override
	public boolean isOwnable() {
		return true;
	}

	private static class FarmPlainInventoryAdapter extends TileInventoryAdapter<TileFarmPlain> {
		public FarmPlainInventoryAdapter(TileFarmPlain tile) {
			super(tile, TileFarmPlain.SLOT_COUNT, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (GuiUtil.isIndexInRange(slotIndex, SLOT_FERTILIZER, SLOT_FERTILIZER_COUNT)) {
				return tile.acceptsAsFertilizer(itemStack);
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_GERMLINGS_1, SLOT_GERMLINGS_COUNT)) {
				return tile.acceptsAsGermling(itemStack);
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_RESOURCES_1, SLOT_RESOURCES_COUNT)) {
				return tile.acceptsAsResource(itemStack);
			} else if (GuiUtil.isIndexInRange(slotIndex, SLOT_CAN, SLOT_CAN_COUNT)) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.tankManager.accepts(fluid);
			}
			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
			return GuiUtil.isIndexInRange(slotIndex, SLOT_PRODUCTION_1, SLOT_PRODUCTION_COUNT);
		}
	}
}
