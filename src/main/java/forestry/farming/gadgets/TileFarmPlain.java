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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import forestry.api.core.BiomeHelper;
import forestry.core.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.core.EnumErrorCode;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.ISocketable;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.DelayTimer;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.TankManager;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Vect;
import forestry.farming.FarmTarget;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.plugins.PluginFarming;

public class TileFarmPlain extends TileFarm implements IFarmHousing, ISocketable, IClimatised, IHintSource, ILiquidTankContainer {

	public static final int SLOT_RESOURCES_1 = 0;
	public static final int SLOT_GERMLINGS_1 = 6;
	public static final int SLOT_PRODUCTION_1 = 12;
	public static final int SLOT_COUNT_RESERVOIRS = 6;
	public static final int SLOT_COUNT_PRODUCTION = 8;

	public static final int SLOT_FERTILIZER = 20;
	public static final int SLOT_CAN = 21;

	public static final int SLOT_COUNT = 22;

	public static final int DELAY_HYDRATION = 100;
	public static final float RAINFALL_MODIFIER_CAP = 15f;
	public static final int BUFFER_FERTILIZER = 200;

	private IFarmLogic[] farmLogics = new IFarmLogic[4];

	private TreeMap<ForgeDirection, FarmTarget[]> targets;
	private int allowedExtent = 0;
	private final DelayTimer checkTimer = new DelayTimer();

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");

	private FilteredTank liquidTank;
	private final TankManager tankManager;

	private IFarmLogic harvestProvider; // The farm logic which supplied the pending crops.
	private final Stack<ICrop> pendingCrops = new Stack<ICrop>();
	private final Stack<ItemStack> pendingProduce = new Stack<ItemStack>();
	private int storedFertilizer;
	private int fertilizerValue = 2000;

	private boolean stage = false;

	private BiomeGenBase biome;

	private int hydrationDelay = 0;
	private int ticksSinceRainfall = 0;

	public TileFarmPlain() {
		fertilizerValue = GameMode.getGameMode().getIntegerSetting("farms.fertilizer.value");
		liquidTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, FluidRegistry.WATER);
		tankManager = new TankManager(liquidTank);
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

		tankManager.readTanksFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		sockets.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("StoredFertilizer", storedFertilizer);
		nbttagcompound.setInteger("HydrationDelay", hydrationDelay);
		nbttagcompound.setInteger("TicksSinceRainfall", ticksSinceRainfall);

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	/* UPDATING */
	@Override
	public void initialize() {
		setBiomeInformation();
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (!isMaster())
			return;

		if (worldObj.isRaining()) {
			if (hydrationDelay > 0)
				hydrationDelay--;
			else
				ticksSinceRainfall = 0;
		} else {
			hydrationDelay = DELAY_HYDRATION;
			if (ticksSinceRainfall < Integer.MAX_VALUE)
				ticksSinceRainfall++;
		}

		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {
			FluidContainerData container = LiquidHelper.getLiquidContainer(inventory.getStackInSlot(SLOT_CAN));
			if (container != null && liquidTank.accepts(container.fluid.getFluid())) {

				inventory.setInventorySlotContents(SLOT_CAN, StackUtils.replenishByContainer(this, inventory.getStackInSlot(SLOT_CAN), container, liquidTank));
				if (inventory.getStackInSlot(SLOT_CAN).stackSize <= 0)
					inventory.setInventorySlotContents(SLOT_CAN, null);
			}
		}

	}

	private void setBiomeInformation() {
		this.biome = Utils.getBiomeAt(worldObj, xCoord, zCoord);
		setErrorState(EnumErrorCode.OK);
	}

	private void createTargets() {

		targets = new TreeMap<ForgeDirection, FarmTarget[]>();

		int groundY = yCoord - 1;
		allowedExtent = 0;

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
				continue;
			// System.out.println(String.format("Master at %s/%s/%s:%s", xCoord, yCoord, zCoord, groundY));
			ArrayList<FarmTarget> potential = new ArrayList<FarmTarget>();

			int xDistance = 0;
			int zDistance = 0;
			Vect candidate = new Vect(xCoord, groundY, zCoord);

			// Determine distance from master TE
			while (true) {
				xDistance += direction.offsetX;
				zDistance += direction.offsetZ;
				// System.out.println(String.format("New offset for %s: x:%s z:%s", direction, xDistance, zDistance));
				// System.out.println(String.format("Validating for %s: x:%s y:%s z:%s", direction, xCoord + xDistance, groundY, zCoord + zDistance));

				TileEntity tile = worldObj.getTileEntity(xCoord + xDistance, groundY, zCoord + zDistance);
				if (tile == null)
					// System.out.println("NUll TE");
					break;
				if (!(tile instanceof IFarmComponent))
					// System.out.println("Not instaceof of farm component");
					break;

				candidate = new Vect(xCoord + xDistance, groundY, zCoord + zDistance);
			}
			// System.out.println(String.format("Determined distance for %s at %s.", direction, candidate));

			// Determine block to start search from
			ForgeDirection search;
			if (direction.offsetX != 0)
				search = ForgeDirection.SOUTH;
			else
				search = ForgeDirection.EAST;

			int xOffset = 0;
			int zOffset = 0;
			Vect start = candidate;
			while (true) {
				xOffset += search.offsetX;
				zOffset += search.offsetZ;

				TileEntity tile = worldObj.getTileEntity(candidate.x + xOffset, candidate.y, candidate.z + zOffset);
				if (tile == null)
					break;
				if (!(tile instanceof IFarmComponent))
					break;

				start = new Vect(candidate.x + xOffset, candidate.y, candidate.z + zOffset);
			}
			// System.out.println(String.format("Determined start block for %s at %s.", direction, candidate));

			ForgeDirection reverse = search.getOpposite();
			ForgeDirection tocenter = direction.getOpposite();
			Vect last = new Vect(start.x + direction.offsetX, start.y, start.z + direction.offsetZ);
			potential.add(new FarmTarget(last));
			while (true) {
				// Switch to next potential block in the farm.
				last = new Vect(last.x + reverse.offsetX, last.y, last.z + reverse.offsetZ);
				// Check validity.
				TileEntity tile = worldObj.getTileEntity(last.x + tocenter.offsetX, last.y, last.z + tocenter.offsetZ);

				// break if we have reached the end of the farm's length.
				if (tile == null)
					break;
				if (!(tile instanceof IFarmComponent))
					break;

				potential.add(new FarmTarget(last));
			}

			// System.out.println(String.format("Adding %s to %s", potential.size(), direction));

			// Set the maximum allowed extent.
			int size = potential.size() * 3;
			if (size > allowedExtent)
				allowedExtent = size;
			targets.put(direction, potential.toArray(new FarmTarget[potential.size()]));
		}

		// Fill out the corners
		// System.out.println("Trying to round corners");
		TreeMap<ForgeDirection, FarmTarget[]> cache = new TreeMap<ForgeDirection, FarmTarget[]>();

		for (Map.Entry<ForgeDirection, FarmTarget[]> entry : targets.entrySet()) {
			ForgeDirection direction = entry.getKey();
			// If the count of possible targets does matches the allowedExtent, we are on the long side and will not process
			if (direction == ForgeDirection.SOUTH || direction == ForgeDirection.NORTH) {
				cache.put(entry.getKey(), entry.getValue());
				continue;
			}

			// Set start and direction to search
			ArrayList<FarmTarget> targ = new ArrayList<FarmTarget>(Arrays.asList(entry.getValue()));
			int sidecount = targ.size();

			FarmTarget start = entry.getValue()[0];
			ForgeDirection search = ForgeDirection.SOUTH;
			int cornerShift = 0;
			if (!Config.squareFarms)
				cornerShift = 1;
			// System.out.println(String.format("Processing start at %s for direction %s.", start.getStart(), direction));

			for (int i = cornerShift; i < allowedExtent + 1; i++) {
				FarmTarget corner = new FarmTarget(new Vect(start.getStart().x + search.offsetX * i, start.getStart().y, start.getStart().z + search.offsetZ
						* i));
				if (!Config.squareFarms) {
					corner.setLimit(allowedExtent - i);
					// System.out.println(String.format("Setting %s at extent %s", corner.getStart().toString(), corner.getExtent()));
					if (corner.getLimit() > 0)
						targ.add(0, corner);
				} else
					targ.add(0, corner);
			}

			search = search.getOpposite();
			for (int i = sidecount; i < sidecount + allowedExtent - cornerShift; i++) {
				FarmTarget corner = new FarmTarget(new Vect(start.getStart().x + search.offsetX * i, start.getStart().y, start.getStart().z + search.offsetZ
						* i));
				if (!Config.squareFarms)
					corner.setLimit(sidecount + allowedExtent - 1 - i);
				// System.out.println(String.format("Setting %s at extent %s", corner.getStart().toString(), corner.getExtent()));
				targ.add(corner);
			}

			cache.put(entry.getKey(), targ.toArray(new FarmTarget[targ.size()]));
		}

		targets = cache;
	}

	private void setExtents() {

		for (Map.Entry<ForgeDirection, FarmTarget[]> entry : targets.entrySet()) {
			ForgeDirection direction = entry.getKey();
			for (FarmTarget target : entry.getValue()) {

				int yOffset;
				for (yOffset = 2; yOffset > -3; yOffset--) {
					Vect position = new Vect(target.getStart().x, target.getStart().y + yOffset, target.getStart().z);
					if (StructureLogicFarm.bricks.contains(worldObj.getBlock(position.x, position.y, position.z)))
						break;
				}
				target.setYOffset(yOffset + 1);

				int extent;
				// Determine extent limit
				int limit = allowedExtent;
				if (target.getLimit() > 0)
					limit = target.getLimit();

				// Determine extent
				for (extent = 0; extent < limit; extent++) {
					Vect position = new Vect(target.getStart().x + direction.offsetX * extent, target.getStart().y + yOffset, target.getStart().z
							+ direction.offsetZ * extent);
					if (!StructureLogicFarm.bricks.contains(worldObj.getBlock(position.x, position.y, position.z)))
						break;
				}

				target.setExtent(extent);
			}
		}
	}

	@Override
	protected void createInventory() {
		inventory = new TileInventoryAdapter(this, SLOT_COUNT, "Items");
	}

	@Override
	public boolean doWork() {

		setBiomeInformation();

		// System.out.println("Starting doWork()");
		if (targets == null) {
			createTargets();
			setExtents();
		} else if (checkTimer.delayPassed(worldObj, 400))
			setExtents();

		if (inventory == null)
			createInventory();

		// System.out.println("Targets set");
		if (tryAddPending())
			return true;

		// System.out.println("Nothing pending added.");
		// Abort if we still have produce waiting.
		if (!pendingProduce.isEmpty()) {
			setErrorState(EnumErrorCode.NOSPACE);
			return false;
		}

		// System.out.println("Product queue empty.");
		if (storedFertilizer <= BUFFER_FERTILIZER) {
			replenishFertilizer();
			if (storedFertilizer <= 0) {
				setErrorState(EnumErrorCode.NOFERTILIZER);
				return false;
			}
		}

		// Cull queued crops.
		if (!pendingCrops.isEmpty())
			if (cullCrop(pendingCrops.peek(), harvestProvider)) {
				pendingCrops.pop();
				return true;
			} else
				return false;

		// Cultivation and collection
		boolean consumedEnergy = false;
		boolean hasFarmland = false;
		boolean hasFertilizer = false;
		boolean hasLiquid = false;

		cycle: for (Map.Entry<ForgeDirection, FarmTarget[]> entry : targets.entrySet()) {
			if (farmLogics.length <= entry.getKey().ordinal() - 2 || farmLogics[entry.getKey().ordinal() - 2] == null)
				continue;

			boolean didWork = false;

			IFarmLogic logic = farmLogics[entry.getKey().ordinal() - 2];

			// Allow listeners to cancel this cycle.
			for (IFarmListener listener : eventHandlers)
				if (listener.cancelTask(logic, entry.getKey()))
					continue cycle;

			// Always try to collect windfall first.
			if (doCollection(logic))
				didWork = true;
			else
				for (int i = 0; i < entry.getValue().length; i++) {

					FarmTarget target = entry.getValue()[i];
					if (target.getExtent() <= 0)
						continue;
					else
						hasFarmland = true;

					if (!stage) {

						// Check fertilizer and water
						if (!hasFertilizer(logic.getFertilizerConsumption()))
							continue;
						else
							hasFertilizer = true;

						FluidStack liquid = LiquidHelper.getLiquid(Defaults.LIQUID_WATER, logic.getWaterConsumption(getHydrationModifier()));
						if (liquid.amount > 0 && !hasLiquid(liquid))
							continue;
						else
							hasLiquid = true;

						if (doCultivationPhase(logic, target, entry.getKey())) {
							// Remove fertilizer and water
							removeFertilizer(logic.getFertilizerConsumption());
							removeLiquid(liquid);

							didWork = true;
						}

					} else {
						hasFertilizer = true;
						hasLiquid = true;
						didWork = doHarvestPhase(logic, target, entry.getKey());
					}

					if (didWork)
						break;
				}

			if (didWork) {
				consumedEnergy = true;
				break;
			}
		}

		if (consumedEnergy)
			setErrorState(EnumErrorCode.OK);
		else if (!hasFarmland)
			setErrorState(EnumErrorCode.NOFARMLAND);
		else if (!hasFertilizer)
			setErrorState(EnumErrorCode.NOFERTILIZER);
		else if (!hasLiquid)
			setErrorState(EnumErrorCode.NOLIQUID);
		else
			setErrorState(EnumErrorCode.OK);
		// Farms alternate between cultivation and harvest.
		stage = !stage;
		return consumedEnergy;
	}

	private boolean doCultivationPhase(IFarmLogic logic, FarmTarget target, ForgeDirection direction) {

		if (logic.cultivate(target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, direction, target.getExtent())) {
			for (IFarmListener listener : eventHandlers)
				listener.hasCultivated(logic, target.getStart().x, target.getStart().y, target.getStart().z, direction, target.getExtent());
			return true;
		}

		return false;
	}

	private boolean doHarvestPhase(IFarmLogic logic, FarmTarget target, ForgeDirection direction) {

		Collection<ICrop> next = logic.harvest(target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, direction,
				target.getExtent());
		if (next == null || next.size() <= 0)
			return false;

		// Let event handlers know.
		for (IFarmListener listener : eventHandlers)
			listener.hasScheduledHarvest(next, logic, target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, direction,
					target.getExtent());

		pendingCrops.addAll(next);
		harvestProvider = logic;
		return true;
	}

	private boolean doCollection(IFarmLogic logic) {

		Collection<ItemStack> collected = logic.collect();
		if (collected == null || collected.size() <= 0)
			return false;

		// Let event handlers know.
		for (IFarmListener listener : eventHandlers)
			listener.hasCollected(collected, logic);

		for (ItemStack produce : collected) {
			addProduceToInventory(produce);
			pendingProduce.push(produce);
		}

		return true;
	}

	private void addProduceToInventory(ItemStack produce) {

		for (IFarmLogic logic : getFarmLogics()) {
			// Germlings try to go into germling slots first.
			if (logic.isAcceptedGermling(produce))
				produce.stackSize -= inventory.addStack(produce, SLOT_GERMLINGS_1, SLOT_COUNT_RESERVOIRS, false, true);
			if (produce.stackSize <= 0)
				return;

			if (logic.isAcceptedResource(produce))
				produce.stackSize -= inventory.addStack(produce, SLOT_RESOURCES_1, SLOT_COUNT_RESERVOIRS, false, true);
			if (produce.stackSize <= 0)
				return;
		}

		produce.stackSize -= inventory.addStack(produce, SLOT_PRODUCTION_1, SLOT_COUNT_PRODUCTION, false, true);
	}

	private boolean cullCrop(ICrop crop, IFarmLogic provider) {

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : eventHandlers)
			if (listener.beforeCropHarvest(crop))
				return true;

		// Check fertilizer
		if (!hasFertilizer(provider.getFertilizerConsumption())) {
			setErrorState(EnumErrorCode.NOFERTILIZER);
			return false;
		}

		// Check water
		FluidStack liquid = LiquidHelper.getLiquid(Defaults.LIQUID_WATER, provider.getWaterConsumption(getHydrationModifier()));
		if (liquid.amount > 0 && !hasLiquid(liquid)) {
			setErrorState(EnumErrorCode.NOLIQUID);
			return false;
		}

		Collection<ItemStack> harvested = crop.harvest();
		if (harvested == null) {
			Proxies.log.fine("Failed to harvest crop: " + crop.toString());
			return true;
		}

		// Remove fertilizer and water
		removeFertilizer(provider.getFertilizerConsumption());
		removeLiquid(liquid);

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : eventHandlers)
			listener.afterCropHarvest(harvested, crop);

		// Stow harvest.
		for (ItemStack harvest : harvested) {

			// Special case germlings
			for (IFarmLogic logic : farmLogics)
				if (logic.isAcceptedGermling(harvest)) {
					harvest.stackSize -= inventory.addStack(harvest, SLOT_GERMLINGS_1, SLOT_COUNT_RESERVOIRS, false, true);
					break;
				}

			// Handle the rest
			if (harvest.stackSize <= 0)
				continue;

			harvest.stackSize -= inventory.addStack(harvest, SLOT_PRODUCTION_1, SLOT_COUNT_PRODUCTION, false, true);
			if (harvest.stackSize <= 0)
				continue;

			pendingProduce.push(harvest);
		}

		return true;
	}

	private boolean tryAddPending() {
		if (pendingProduce.isEmpty())
			return false;

		ItemStack next = pendingProduce.peek();
		if (inventory.tryAddStack(next, SLOT_PRODUCTION_1, SLOT_COUNT_PRODUCTION, true, true)) {
			pendingProduce.pop();
			return true;
		}

		setErrorState(EnumErrorCode.NOSPACE);
		return false;
	}

	/* FERTILIZER HANDLING */
	private void replenishFertilizer() {
		if(fertilizerValue < 0) {
			storedFertilizer += 2000;
			return;
		}

		ItemStack fertilizer = inventory.getStackInSlot(SLOT_FERTILIZER);
		if (fertilizer == null || fertilizer.stackSize <= 0)
			return;

		if (!acceptsAsFertilizer(fertilizer))
			return;

		inventory.decrStackSize(SLOT_FERTILIZER, 1);
		storedFertilizer += fertilizerValue;
	}

	private boolean hasFertilizer(int amount) {
		if(fertilizerValue < 0)
			return true;

		return storedFertilizer >= amount;
	}

	private void removeFertilizer(int amount) {

		if(fertilizerValue < 0)
			return;

		storedFertilizer -= amount;
		if (storedFertilizer < 0)
			storedFertilizer = 0;
	}

	public int getStoredFertilizerScaled(int scale) {
		if (storedFertilizer == 0)
			return 0;

		return (storedFertilizer * scale) / (fertilizerValue + BUFFER_FERTILIZER);
	}

	/* STRUCTURE HANDLING */
	@Override
	public void makeMaster() {
		super.makeMaster();
		refreshFarmLogics();
	}

	@Override
	public void onStructureReset() {
		super.onStructureReset();
	}

	public StandardTank[] getTanks() {
		return new StandardTank[] {liquidTank};
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public void setFarmLogic(ForgeDirection direction, IFarmLogic logic) {
		farmLogics[direction.ordinal() - 2] = logic;
	}

	@Override
	public void resetFarmLogic(ForgeDirection direction) {
		farmLogics[direction.ordinal() - 2] = new FarmLogicArboreal(this);
	}

	public IFarmLogic[] getFarmLogics() {
		return farmLogics;
	}

	private void refreshFarmLogics() {

		farmLogics = new IFarmLogic[] { new FarmLogicArboreal(this), new FarmLogicArboreal(this), new FarmLogicArboreal(this), new FarmLogicArboreal(this) };

		// See whether we have socketed stuff.
		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null)
				chipset.onLoad(this);
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

		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack))
			return;

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null)
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null)
					chipset.onRemoval(this);
			}

		sockets.setInventorySlotContents(slot, stack);
		refreshFarmLogics();

		if (stack == null) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null)
			chipset.onInsertion(this);
	}

	/* IFARMHOUSING */
	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public boolean hasResources(ItemStack[] resources) {
		return inventory.contains(resources, SLOT_RESOURCES_1, SLOT_COUNT_RESERVOIRS);
	}

	public boolean hasResourcesAmount(int amount) {
		return inventory.containsAmount(amount, SLOT_RESOURCES_1, SLOT_COUNT_RESERVOIRS);
	}

	public boolean hasGermlingsPercent(float percent) {
		return inventory.containsPercent(percent, SLOT_GERMLINGS_1, SLOT_COUNT_RESERVOIRS);
	}

	public boolean hasFertilizerPercent(float percent) {
		return inventory.containsPercent(percent, SLOT_FERTILIZER, 1);
	}

	@Override
	public void removeResources(ItemStack[] resources) {
		EntityPlayer player = Proxies.common.getPlayer(worldObj, owner);
		inventory.removeSets(1, resources, SLOT_RESOURCES_1, SLOT_COUNT_RESERVOIRS, player, false, true, true);
	}

	@Override
	public boolean hasLiquid(FluidStack liquid) {
		return liquidTank.getFluidAmount() >= liquid.amount;
	}

	@Override
	public void removeLiquid(FluidStack liquid) {
		liquidTank.drain(liquid.amount, true);
	}

	public boolean hasGermlings(ItemStack[] germlings) {
		return inventory.contains(germlings, SLOT_GERMLINGS_1, SLOT_COUNT_RESERVOIRS);
	}

	@Override
	public boolean plantGermling(IFarmable germling, World world, int x, int y, int z) {

		for (int i = SLOT_GERMLINGS_1; i < SLOT_GERMLINGS_1 + SLOT_COUNT_RESERVOIRS; i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (!germling.isGermling(inventory.getStackInSlot(i)))
				continue;

			EntityPlayer player = Proxies.common.getPlayer(world, owner);
			if (!germling.plantSaplingAt(player, inventory.getStackInSlot(i), world, x, y, z))
				continue;

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
		if (itemstack == null)
			return false;
		if (farmLogics == null)
			return false;

		for (IFarmLogic logic : farmLogics)
			if (logic.isAcceptedGermling(itemstack))
				return true;

		return false;
	}

	@Override
	public boolean acceptsAsResource(ItemStack itemstack) {
		if (itemstack == null)
			return false;
		if (farmLogics == null)
			return false;

		for (IFarmLogic logic : farmLogics)
			if (logic.isAcceptedResource(itemstack))
				return true;

		return false;
	}

	@Override
	public boolean acceptsAsFertilizer(ItemStack itemstack) {
		if (itemstack == null)
			return false;

		return StackUtils.isIdenticalItem(PluginFarming.farmFertilizer, itemstack);
	}

	int[] coords;
	int[] offset;
	int[] area;

	@Override
	public int[] getCoords() {
		if (coords == null)
			coords = new int[] { xCoord, yCoord, zCoord };
		return coords;
	}

	@Override
	public int[] getOffset() {
		if (offset == null)
			offset = new int[]{-getArea()[0] / 2, -2, -getArea()[2] / 2};
		return offset;
	}

	@Override
	public int[] getArea() {
		if (area == null)
			area = new int[] { 7 + (allowedExtent * 2), 13, 7 + (allowedExtent * 2) };
		return area;
	}

	/* NETWORK GUI */
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
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
		if (BiomeHelper.isBiomeHellish(biome))
			return EnumTemperature.HELLISH;

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

	/* ILiquidTankContainer */
	@Override
	public int fill(ForgeDirection direction, FluidStack resource, boolean doFill) {
		return tankManager.fill(direction, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tankManager.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tankManager.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
	}
}
