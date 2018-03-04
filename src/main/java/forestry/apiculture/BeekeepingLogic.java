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
package forestry.apiculture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.network.packets.PacketBeeLogicActive;
import forestry.apiculture.network.packets.PacketBeeLogicActiveEntity;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;

public class BeekeepingLogic implements IBeekeepingLogic {

	private static final int totalBreedingTime = Constants.APIARY_BREEDING_TIME;

	private final IBeeHousing housing;
	private final IBeeModifier beeModifier;
	private final IBeeListener beeListener;

	private int beeProgress;
	private int beeProgressMax;

	private int queenWorkCycleThrottle;
	private IEffectData effectData[] = new IEffectData[2];

	private final Stack<ItemStack> spawn = new Stack<>();

	private final HasFlowersCache hasFlowersCache = new HasFlowersCache();
	private final QueenCanWorkCache queenCanWorkCache = new QueenCanWorkCache();
	private final PollenHandler pollenHandler = new PollenHandler();

	// Client
	private boolean active;
	private IBee queen;
	private ItemStack queenStack; // used to detect server changes and sync clientQueen

	public BeekeepingLogic(IBeeHousing housing) {
		this.housing = housing;
		this.beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		this.beeListener = BeeManager.beeRoot.createBeeHousingListener(housing);
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		beeProgress = nbttagcompound.getInteger("BreedingTime");
		queenWorkCycleThrottle = nbttagcompound.getInteger("Throttle");

		NBTTagCompound queenNBT = nbttagcompound.getCompoundTag("queen");
		queenStack = ItemStack.loadItemStackFromNBT(queenNBT);
		queen = BeeManager.beeRoot.getMember(queenStack);

		setActive(nbttagcompound.getBoolean("Active"));

		hasFlowersCache.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("Offspring", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			spawn.add(ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i)));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("BreedingTime", beeProgress);
		nbttagcompound.setInteger("Throttle", queenWorkCycleThrottle);

		if (queenStack != null) {
			NBTTagCompound queenNBT = new NBTTagCompound();
			queenStack.writeToNBT(queenNBT);
			nbttagcompound.setTag("queen", queenNBT);
		}

		nbttagcompound.setBoolean("Active", active);

		hasFlowersCache.writeToNBT(nbttagcompound);

		Stack<ItemStack> spawnCopy = new Stack<>();
		spawnCopy.addAll(spawn);
		NBTTagList nbttaglist = new NBTTagList();
		while (!spawnCopy.isEmpty()) {
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			spawnCopy.pop().writeToNBT(nbttagcompound1);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("Offspring", nbttaglist);
		return nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeBoolean(active);
		if (active) {
			data.writeItemStack(queenStack);
			hasFlowersCache.writeData(data);
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		boolean active = data.readBoolean();
		setActive(active);
		if (active) {
			queenStack = data.readItemStack();
			queen = BeeManager.beeRoot.getMember(queenStack);
			hasFlowersCache.readData(data);
		}
	}

	/* Activatable */
	private void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		this.active = active;

		syncToClient();
	}

	/* UPDATING */

	@Override
	public boolean canWork() {

		IErrorLogic errorLogic = housing.getErrorLogic();
		errorLogic.clearErrors();

		IBeeHousingInventory beeInventory = housing.getBeeInventory();

		boolean hasSpace = addPendingProducts(beeInventory, spawn);
		errorLogic.setCondition(!hasSpace, EnumErrorCode.NO_SPACE_INVENTORY);

		ItemStack queenStack = beeInventory.getQueen();
		EnumBeeType beeType = BeeManager.beeRoot.getType(queenStack);
		// check if we're breeding
		if (beeType == EnumBeeType.PRINCESS) {
			boolean hasDrone = BeeManager.beeRoot.isDrone(beeInventory.getDrone());
			errorLogic.setCondition(!hasDrone, EnumErrorCode.NO_DRONE);

			setActive(false); // not active (no bee FX) when we are breeding
			return !errorLogic.hasErrors();
		}

		if (beeType == EnumBeeType.QUEEN) {
			if (!isQueenAlive(queenStack)) {
				IBee dyingQueen = BeeManager.beeRoot.getMember(queenStack);
				Collection<ItemStack> spawned = killQueen(dyingQueen, housing, beeListener);
				spawn.addAll(spawned);
				queenStack = null;
			}
		} else {
			queenStack = null;
		}

		if (this.queenStack != queenStack) {
			this.queen = BeeManager.beeRoot.getMember(queenStack);
			this.queenStack = queenStack;
			hasFlowersCache.clear();
			queenCanWorkCache.clear();
		}

		if (errorLogic.setCondition(queen == null, EnumErrorCode.NO_QUEEN)) {
			setActive(false);
			beeProgress = 0;
			return false;
		}

		Set<IErrorState> queenErrors = queenCanWorkCache.queenCanWork(queen, housing);
		for (IErrorState errorState : queenErrors) {
			errorLogic.setCondition(true, errorState);
		}

		hasFlowersCache.update(queen, housing);
		boolean hasFlowers = hasFlowersCache.hasFlowers();
		boolean flowerCacheNeedsSync = hasFlowersCache.needsSync();
		errorLogic.setCondition(!hasFlowers, EnumErrorCode.NO_FLOWER);

		boolean canWork = !errorLogic.hasErrors();
		if (active != canWork) {
			setActive(canWork);
		} else if (flowerCacheNeedsSync) {
			syncToClient();
		}
		return canWork;
	}

	@Override
	public void doWork() {
		IBeeHousingInventory beeInventory = housing.getBeeInventory();
		ItemStack queenStack = beeInventory.getQueen();
		EnumBeeType beeType = BeeManager.beeRoot.getType(queenStack);
		if (beeType == EnumBeeType.PRINCESS) {
			tickBreed();
		} else if (beeType == EnumBeeType.QUEEN) {
			queenWorkTick(queen, queenStack);
		}
	}

	@Override
	public void clearCachedValues() {
		if (!housing.getWorldObj().isRemote) {
			hasFlowersCache.clear();
			queenCanWorkCache.clear();
			canWork();
		}
	}

	private void queenWorkTick(@Nullable IBee queen, @Nonnull ItemStack queenStack) {
		if (queen == null) {
			beeProgress = 0;
			beeProgressMax = 0;
			return;
		}

		// Effects only fire when queen can work.
		effectData = queen.doEffect(effectData, housing);

		// Work cycles are throttled, rather than occurring every game tick.
		queenWorkCycleThrottle++;
		if (queenWorkCycleThrottle >= PluginApiculture.ticksPerBeeWorkCycle) {
			queenWorkCycleThrottle = 0;

			doProduction(queen, housing, beeListener);
			queen.plantFlowerRandom(housing);
			pollenHandler.doPollination(queen, housing, beeListener);

			// Age the queen
			float lifespanModifier = beeModifier.getLifespanModifier(queen.getGenome(), queen.getMate(), 1.0f);
			queen.age(housing.getWorldObj(), lifespanModifier);

			// Write the changed queen back into the item stack.
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			queen.writeToNBT(nbttagcompound);
			queenStack.setTagCompound(nbttagcompound);
			housing.getBeeInventory().setQueen(queenStack);
		}

		beeProgress = queen.getHealth();
		beeProgressMax = queen.getMaxHealth();
	}

	private static void doProduction(IBee queen, IBeeHousing beeHousing, IBeeListener beeListener) {
		// Produce and add stacks
		ItemStack[] products = queen.produceStacks(beeHousing);
		if (products == null) {
			return;
		}
		beeListener.wearOutEquipment(1);

		IBeeHousingInventory beeInventory = beeHousing.getBeeInventory();

		for (ItemStack stack : products) {
			beeInventory.addProduct(stack, false);
		}
	}

	private static boolean addPendingProducts(IBeeHousingInventory beeInventory, Stack<ItemStack> spawn) {
		boolean housingHasSpace = true;

		while (!spawn.isEmpty()) {
			ItemStack next = spawn.peek();
			if (beeInventory.addProduct(next, true)) {
				spawn.pop();
			} else {
				housingHasSpace = false;
				break;
			}
		}

		return housingHasSpace;
	}

	/** Checks if a queen is alive. Much faster than reading the whole bee nbt */
	private static boolean isQueenAlive(ItemStack queenStack) {
		if (queenStack == null) {
			return false;
		}
		NBTTagCompound nbtTagCompound = queenStack.getTagCompound();
		if (nbtTagCompound == null) {
			return false;
		}
		int health = nbtTagCompound.getInteger("Health");
		return health > 0;
	}

	// / BREEDING
	private void tickBreed() {
		beeProgressMax = totalBreedingTime;

		IBeeHousingInventory beeInventory = housing.getBeeInventory();

		ItemStack droneStack = beeInventory.getDrone();
		ItemStack princessStack = beeInventory.getQueen();

		EnumBeeType droneType = BeeManager.beeRoot.getType(droneStack);
		EnumBeeType princessType = BeeManager.beeRoot.getType(princessStack);
		if (droneType != EnumBeeType.DRONE || princessType != EnumBeeType.PRINCESS) {
			beeProgress = 0;
			return;
		}

		if (beeProgress < totalBreedingTime) {
			beeProgress++;
		}
		if (beeProgress < totalBreedingTime) {
			return;
		}

		// Mate and replace princess with queen
		IBee princess = BeeManager.beeRoot.getMember(princessStack);
		IBee drone = BeeManager.beeRoot.getMember(droneStack);
		princess.mate(drone);

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		princess.writeToNBT(nbttagcompound);
		queenStack = new ItemStack(PluginApiculture.items.beeQueenGE);
		queenStack.setTagCompound(nbttagcompound);

		beeInventory.setQueen(queenStack);

		// Register the new queen with the breeding tracker
		BeeManager.beeRoot.getBreedingTracker(housing.getWorldObj(), housing.getOwner()).registerQueen(princess);

		// Remove drone
		beeInventory.getDrone().stackSize--;
		if (beeInventory.getDrone().stackSize <= 0) {
			beeInventory.setDrone(null);
		}

		// Reset breeding time
		queen = princess;
		beeProgress = princess.getHealth();
		beeProgressMax = princess.getMaxHealth();
	}

	private static Collection<ItemStack> killQueen(IBee queen, IBeeHousing beeHousing, IBeeListener beeListener) {
		if (queen == null) {
			return Collections.emptySet();
		}

		IBeeHousingInventory beeInventory = beeHousing.getBeeInventory();

		Collection<ItemStack> spawn;

		if (queen.canSpawn()) {
			spawn = spawnOffspring(queen, beeHousing);
			beeListener.onQueenDeath();
			beeInventory.getQueen().stackSize = 0;
			beeInventory.setQueen(null);
		} else {
			Log.warning("Tried to spawn offspring off an unmated queen. Devolving her to a princess.");

			ItemStack convert = new ItemStack(PluginApiculture.items.beePrincessGE);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			queen.writeToNBT(nbttagcompound);
			convert.setTagCompound(nbttagcompound);

			spawn = Collections.singleton(convert);
			beeInventory.setQueen(null);
		}

		return spawn;
	}

	/**
	 * Creates the succeeding princess and between one and three drones.
	 */
	private static Collection<ItemStack> spawnOffspring(IBee queen, IBeeHousing beeHousing) {

		World world = beeHousing.getWorldObj();

		Stack<ItemStack> offspring = new Stack<>();
		IApiaristTracker breedingTracker = BeeManager.beeRoot.getBreedingTracker(world, beeHousing.getOwner());

		// Princess
		boolean secondPrincess = world.rand.nextInt(10000) < PluginApiculture.getSecondPrincessChance() * 100;
		int count = secondPrincess ? 2 : 1;
		while (count > 0) {
			count--;
			IBee heiress = queen.spawnPrincess(beeHousing);
			if (heiress != null) {
				ItemStack princess = BeeManager.beeRoot.getMemberStack(heiress, EnumBeeType.PRINCESS);
				breedingTracker.registerPrincess(heiress);
				offspring.push(princess);
			}
		}

		// Drones
		IBee[] drones = queen.spawnDrones(beeHousing);
		for (IBee drone : drones) {
			ItemStack droneStack = BeeManager.beeRoot.getMemberStack(drone, EnumBeeType.DRONE);
			breedingTracker.registerDrone(drone);
			offspring.push(droneStack);
		}

		IBeeHousingInventory beeInventory = beeHousing.getBeeInventory();

		Collection<ItemStack> spawn = new ArrayList<>();

		while (!offspring.isEmpty()) {
			ItemStack spawned = offspring.pop();
			if (!beeInventory.addProduct(spawned, true)) {
				spawn.add(spawned);
			}
		}

		return spawn;
	}

	/* CLIENT */

	@Override
	public void syncToClient() {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			if (housing instanceof Entity) {
				Proxies.net.sendNetworkPacket(new PacketBeeLogicActiveEntity(housing, (Entity) housing), world);
			} else {
				Proxies.net.sendNetworkPacket(new PacketBeeLogicActive(housing), world);
			}
		}
	}

	@Override
	public void syncToClient(EntityPlayerMP player) {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			if (housing instanceof TileEntity) {
				Proxies.net.sendToPlayer(new PacketBeeLogicActive(housing), player);
			} else if (housing instanceof Entity) {
				Proxies.net.sendToPlayer(new PacketBeeLogicActiveEntity(housing, (Entity) housing), player);
			}
		}
	}

	@Override
	public int getBeeProgressPercent() {
		if (beeProgressMax == 0) {
			return 0;
		}

		return Math.round(beeProgress * 100f / beeProgressMax);
	}

	@Override
	public boolean canDoBeeFX() {
		if(Proxies.common.getClientInstance().isGamePaused()){
			return false;
		}
		return active;
	}

	@Override
	public void doBeeFX() {
		if (queen != null) {
			queen.doFX(effectData, housing);
		}
	}

	@Override
	@Nonnull
	public List<BlockPos> getFlowerPositions() {
		return hasFlowersCache.getFlowerCoords();
	}

	private static class QueenCanWorkCache {
		private static final int ticksPerCheckQueenCanWork = 10;

		private Set<IErrorState> queenCanWorkCached = Collections.emptySet();
		private int queenCanWorkCooldown = 0;

		public Set<IErrorState> queenCanWork(IBee queen, IBeeHousing beeHousing) {
			if (queenCanWorkCooldown <= 0) {
				queenCanWorkCached = queen.getCanWork(beeHousing);
				queenCanWorkCooldown = ticksPerCheckQueenCanWork;
			} else {
				queenCanWorkCooldown--;
			}

			return queenCanWorkCached;
		}

		public void clear() {
			queenCanWorkCached.clear();
			queenCanWorkCooldown = 0;
		}
	}

	private static class PollenHandler {
		private static final int MAX_POLLINATION_ATTEMPTS = 20;

		private IIndividual pollen;
		private int attemptedPollinations = 0;

		public void doPollination(IBee queen, IBeeHousing beeHousing, IBeeListener beeListener) {
			// Get pollen if none available yet
			if (pollen == null) {
				attemptedPollinations = 0;
				pollen = queen.retrievePollen(beeHousing);
				if (pollen != null) {
					if (beeListener.onPollenRetrieved(pollen)) {
						pollen = null;
					}
				}
			}

			if (pollen != null) {
				attemptedPollinations++;
				if (queen.pollinateRandom(beeHousing, pollen) || attemptedPollinations >= MAX_POLLINATION_ATTEMPTS) {
					pollen = null;
				}
			}
		}
	}

}
