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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.network.packets.PacketBeeLogicActive;
import forestry.apiculture.network.packets.PacketBeeLogicActiveEntity;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.utils.Log;
import forestry.core.utils.NetworkUtil;

public class BeekeepingLogic implements IBeekeepingLogic {

	private static final int totalBreedingTime = Constants.APIARY_BREEDING_TIME;

	private final IBeeHousing housing;
	private final IBeeModifier beeModifier;
	private final IBeeListener beeListener;

	private int beeProgress;
	private int beeProgressMax;

	private int queenWorkCycleThrottle;
	private IEffectData[] effectData = new IEffectData[2];

	private final Stack<ItemStack> spawn = new Stack<>();

	private final HasFlowersCache hasFlowersCache = new HasFlowersCache();
	private final QueenCanWorkCache queenCanWorkCache = new QueenCanWorkCache();
	private final PollenHandler pollenHandler = new PollenHandler();

	// Client
	private boolean active;
	@Nullable
	private IBee queen;
	private ItemStack queenStack = ItemStack.EMPTY; // used to detect server changes and sync clientQueen

	public BeekeepingLogic(IBeeHousing housing) {
		this.housing = housing;
		this.beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		this.beeListener = BeeManager.beeRoot.createBeeHousingListener(housing);
	}

	// / SAVING & LOADING
	@Override
	public void read(CompoundNBT compoundNBT) {
		beeProgress = compoundNBT.getInt("BreedingTime");
		queenWorkCycleThrottle = compoundNBT.getInt("Throttle");

		if (compoundNBT.contains("queen")) {
			CompoundNBT queenNBT = compoundNBT.getCompound("queen");
			queenStack = ItemStack.read(queenNBT);
			queen = BeeManager.beeRoot.create(queenStack).orElse(null);
		}

		setActive(compoundNBT.getBoolean("Active"));

		hasFlowersCache.read(compoundNBT);

		ListNBT nbttaglist = compoundNBT.getList("Offspring", 10);
		for (int i = 0; i < nbttaglist.size(); i++) {
			spawn.add(ItemStack.read(nbttaglist.getCompound(i)));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT.putInt("BreedingTime", beeProgress);
		compoundNBT.putInt("Throttle", queenWorkCycleThrottle);

		if (!queenStack.isEmpty()) {
			CompoundNBT queenNBT = new CompoundNBT();
			queenStack.write(queenNBT);
			compoundNBT.put("queen", queenNBT);
		}

		compoundNBT.putBoolean("Active", active);

		hasFlowersCache.write(compoundNBT);

		Stack<ItemStack> spawnCopy = new Stack<>();
		spawnCopy.addAll(spawn);
		ListNBT nbttaglist = new ListNBT();
		while (!spawnCopy.isEmpty()) {
			CompoundNBT compoundNBT1 = new CompoundNBT();
			spawnCopy.pop().write(compoundNBT1);
			nbttaglist.add(compoundNBT1);
		}
		compoundNBT.put("Offspring", nbttaglist);
		return compoundNBT;
	}

	@Override
	public void writeData(PacketBuffer data) {
		data.writeBoolean(active);
		if (active) {
			data.writeItemStack(queenStack);
			hasFlowersCache.writeData(data);
		}
	}

	@Override
	public void readData(PacketBuffer data) throws IOException {
		boolean active = data.readBoolean();
		setActive(active);
		if (active) {
			queenStack = data.readItemStack();
			queen = BeeManager.beeRoot.create(queenStack).orElse(null);
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
		Optional<IOrganismType> optionalType = BeeManager.beeRoot.getTypes().getType(queenStack);
		if (!optionalType.isPresent()) {
			return false;
		}
		IOrganismType beeType = optionalType.get();
		// check if we're breeding
		if (beeType == EnumBeeType.PRINCESS) {
			boolean hasDrone = BeeManager.beeRoot.isDrone(beeInventory.getDrone());
			errorLogic.setCondition(!hasDrone, EnumErrorCode.NO_DRONE);

			setActive(false); // not active (no bee FX) when we are breeding
			return !errorLogic.hasErrors();
		}
		if (beeType == EnumBeeType.QUEEN) {
			if (!isQueenAlive(queenStack)) {
				IBee dyingQueen = BeeManager.beeRoot.create(queenStack).orElse(null);
				Collection<ItemStack> spawned = killQueen(dyingQueen, housing, beeListener);
				spawn.addAll(spawned);
				queenStack = ItemStack.EMPTY;
			}
		} else {
			queenStack = ItemStack.EMPTY;
		}

		if (this.queenStack != queenStack) {
			if (!queenStack.isEmpty()) {
				this.queen = BeeManager.beeRoot.create(queenStack).orElse(null);
				if (this.queen != null) {
					hasFlowersCache.onNewQueen(queen, housing);
				}
			} else {
				this.queen = null;
			}
			this.queenStack = queenStack;
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
		Optional<IOrganismType> beeType = BeeManager.beeRoot.getTypes().getType(queenStack);
		beeType.ifPresent(type -> {
			if (type == EnumBeeType.PRINCESS) {
				tickBreed();
			} else if (type == EnumBeeType.QUEEN) {
				queenWorkTick(queen, queenStack);
			}
		});
	}

	@Override
	public void clearCachedValues() {
		if (!housing.getWorldObj().isRemote) {
			queenCanWorkCache.clear();
			canWork();
			if (queen != null) {
				hasFlowersCache.forceLookForFlowers(queen, housing);
			}
		}
	}

	private void queenWorkTick(@Nullable IBee queen, ItemStack queenStack) {
		if (queen == null) {
			beeProgress = 0;
			beeProgressMax = 0;
			return;
		}

		// Effects only fire when queen can work.
		effectData = queen.doEffect(effectData, housing);

		// Work cycles are throttled, rather than occurring every game tick.
		queenWorkCycleThrottle++;
		if (queenWorkCycleThrottle >= ModuleApiculture.ticksPerBeeWorkCycle) {
			queenWorkCycleThrottle = 0;

			doProduction(queen, housing, beeListener);
			World world = housing.getWorldObj();
			List<BlockState> flowers = hasFlowersCache.getFlowers(world);
			if (flowers.size() < ModuleApiculture.maxFlowersSpawnedPerHive) {
				queen.plantFlowerRandom(housing, flowers).ifPresent(hasFlowersCache::addFlowerPos);
			}
			pollenHandler.doPollination(queen, housing, beeListener);

			// Age the queen
			IGenome mate = queen.getMate().get();
			float lifespanModifier = beeModifier.getLifespanModifier(queen.getGenome(), mate, 1.0f);
			queen.age(world, lifespanModifier);

			// Write the changed queen back into the item stack.
			CompoundNBT compound = new CompoundNBT();
			queen.write(compound);
			queenStack.setTag(compound);
			housing.getBeeInventory().setQueen(queenStack);
		}

		beeProgress = queen.getHealth();
		beeProgressMax = queen.getMaxHealth();
	}

	private static void doProduction(IBee queen, IBeeHousing beeHousing, IBeeListener beeListener) {
		// Produce and add stacks
		List<ItemStack> products = queen.produceStacks(beeHousing);
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

	/**
	 * Checks if a queen is alive. Much faster than reading the whole bee nbt
	 */
	private static boolean isQueenAlive(ItemStack queenStack) {
		if (queenStack.isEmpty()) {
			return false;
		}
		CompoundNBT compound = queenStack.getTag();
		if (compound == null) {
			return false;
		}
		int health = compound.getInt("Health");
		return health > 0;
	}

	// / BREEDING
	private void tickBreed() {
		beeProgressMax = totalBreedingTime;

		IBeeHousingInventory beeInventory = housing.getBeeInventory();

		ItemStack droneStack = beeInventory.getDrone();
		ItemStack princessStack = beeInventory.getQueen();

		Optional<IOrganismType> droneType = BeeManager.beeRoot.getTypes().getType(droneStack);
		Optional<IOrganismType> princessType = BeeManager.beeRoot.getTypes().getType(princessStack);
		if (droneType.filter(type -> type != EnumBeeType.DRONE).isPresent() || princessType.filter(type -> type != EnumBeeType.PRINCESS).isPresent()) {
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
		Optional<IBee> optionalPrincess = BeeManager.beeRoot.create(princessStack);
		Optional<IBee> optionalDrone = BeeManager.beeRoot.create(droneStack);
		if (!optionalPrincess.isPresent() || !optionalDrone.isPresent()) {
			return;
		}
		IBee princess = optionalPrincess.get();
		IBee drone = optionalDrone.get();
		princess.mate(drone.getGenome());

		CompoundNBT compound = new CompoundNBT();
		princess.write(compound);
		queenStack = new ItemStack(ModuleApiculture.getItems().beeQueenGE);
		queenStack.setTag(compound);

		beeInventory.setQueen(queenStack);

		// Register the new queen with the breeding tracker
		//TODO world cast
		BeeManager.beeRoot.getBreedingTracker(housing.getWorldObj(), housing.getOwner()).registerQueen(princess);

		// Remove drone
		beeInventory.getDrone().shrink(1);

		// Reset breeding time
		queen = princess;
		beeProgress = princess.getHealth();
		beeProgressMax = princess.getMaxHealth();
	}

	private static Collection<ItemStack> killQueen(IBee queen, IBeeHousing beeHousing, IBeeListener beeListener) {
		IBeeHousingInventory beeInventory = beeHousing.getBeeInventory();

		Collection<ItemStack> spawn;

		if (queen.canSpawn()) {
			spawn = spawnOffspring(queen, beeHousing);
			beeListener.onQueenDeath();
			beeInventory.getQueen().setCount(0);
			beeInventory.setQueen(ItemStack.EMPTY);
		} else {
			Log.warning("Tried to spawn offspring off an unmated queen. Devolving her to a princess.");

			ItemStack convert = new ItemStack(ModuleApiculture.getItems().beePrincessGE);
			CompoundNBT CompoundNBT = new CompoundNBT();
			queen.write(CompoundNBT);
			convert.setTag(CompoundNBT);

			spawn = Collections.singleton(convert);
			beeInventory.setQueen(ItemStack.EMPTY);
		}

		return spawn;
	}

	/**
	 * Creates the succeeding princess and between one and three drones.
	 */
	private static Collection<ItemStack> spawnOffspring(IBee queen, IBeeHousing beeHousing) {

		World world = beeHousing.getWorldObj();

		Stack<ItemStack> offspring = new Stack<>();
		//TODO world cast
		IApiaristTracker breedingTracker = BeeManager.beeRoot.getBreedingTracker(world, beeHousing.getOwner());

		// Princess
		boolean secondPrincess = world.rand.nextInt(10000) < ModuleApiculture.getSecondPrincessChance() * 100;
		int count = secondPrincess ? 2 : 1;
		while (count > 0) {
			count--;
			Optional<IBee> optionalHeiress = queen.spawnPrincess(beeHousing);
			if (optionalHeiress.isPresent()) {
				IBee heiress = optionalHeiress.get();
				ItemStack princess = BeeManager.beeRoot.getTypes().createStack(heiress, EnumBeeType.PRINCESS);
				breedingTracker.registerPrincess(heiress);
				offspring.push(princess);
			}
		}

		// Drones
		List<IBee> drones = queen.spawnDrones(beeHousing);
		for (IBee drone : drones) {
			ItemStack droneStack = BeeManager.beeRoot.getTypes().createStack(drone, EnumBeeType.DRONE);
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
				Entity housingEntity = (Entity) this.housing;
				NetworkUtil.sendNetworkPacket(new PacketBeeLogicActiveEntity(this.housing, housingEntity), housingEntity.getPosition(), world);
			} else {
				NetworkUtil.sendNetworkPacket(new PacketBeeLogicActive(housing), housing.getCoordinates(), world);
			}
		}
	}

	@Override
	public void syncToClient(ServerPlayerEntity player) {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			if (housing instanceof TileEntity) {
				NetworkUtil.sendToPlayer(new PacketBeeLogicActive(housing), player);
			} else if (housing instanceof Entity) {
				NetworkUtil.sendToPlayer(new PacketBeeLogicActiveEntity(housing, (Entity) housing), player);
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
	@OnlyIn(Dist.CLIENT)
	public boolean canDoBeeFX() {
		return !Minecraft.getInstance().isGamePaused() && active;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doBeeFX() {
		if (queen != null) {
			queen.doFX(effectData, housing);
		}
	}

	@Override
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

		@Nullable
		private IIndividual pollen;
		private int attemptedPollinations = 0;

		public void doPollination(IBee queen, IBeeHousing beeHousing, IBeeListener beeListener) {
			// Get pollen if none available yet
			if (pollen == null) {
				attemptedPollinations = 0;
				pollen = queen.retrievePollen(beeHousing).orElse(null);
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
