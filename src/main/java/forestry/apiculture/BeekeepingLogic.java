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

import java.util.Stack;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginApiculture;

public class BeekeepingLogic implements IBeekeepingLogic {

	private static final int MAX_POLLINATION_ATTEMPTS = 20;
	private final IBeeHousing housing;
	// Breeding
	private int breedingTime;
	private final int totalBreedingTime = Defaults.APIARY_BREEDING_TIME;
	private int queenWorkCycleThrottle;
	private IEffectData effectData[] = new IEffectData[2];
	private IBee queen;
	private IIndividual pollen;
	private int attemptedPollinations = 0;
	private final Stack<ItemStack> spawn = new Stack<ItemStack>();

	public BeekeepingLogic(IBeeHousing housing) {
		this.housing = housing;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		breedingTime = nbttagcompound.getInteger("BreedingTime");
		queenWorkCycleThrottle = nbttagcompound.getInteger("Throttle");

		NBTTagList nbttaglist = nbttagcompound.getTagList("Offspring", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			spawn.add(ItemStack.loadItemStackFromNBT(nbttaglist.getCompoundTagAt(i)));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("BreedingTime", breedingTime);
		nbttagcompound.setInteger("Throttle", queenWorkCycleThrottle);

		Stack<ItemStack> spawnCopy = new Stack<ItemStack>();
		spawnCopy.addAll(spawn);
		NBTTagList nbttaglist = new NBTTagList();
		while (!spawnCopy.isEmpty()) {
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			spawnCopy.pop().writeToNBT(nbttagcompound1);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("Offspring", nbttaglist);
		
	}

	// / STATE INFORMATION
	@Override
	public int getBreedingTime() {
		return this.breedingTime;
	}

	@Override
	public int getTotalBreedingTime() {
		return this.totalBreedingTime;
	}

	@Override
	public IBee getQueen() {
		return this.queen;
	}

	@Override
	public IBeeHousing getHousing() {
		return this.housing;
	}

	@Override
	public IEffectData[] getEffectData() {
		return this.effectData;
	}

	/* UPDATING */
	@Override
	public void update() {

		this.queen = null;

		if (!addPendingProducts()) {
			return;
		}
		
		if (tryBreedingPrincess() || !hasHealthyQueen()) {
			return;
		}

		if (!queenCanWork()) {
			return;
		}

		queenWorkTick();
	}

	private void queenWorkTick() {
		// Effects only fire when queen can work.
		effectData = queen.doEffect(effectData, housing);

		// Work cycles are throttled, rather than occurring every game tick.
		queenWorkCycleThrottle++;
		if (queenWorkCycleThrottle >= PluginApiculture.ticksPerBeeWorkCycle) {
			queenWorkCycleThrottle = 0;
			
			// Need a flower
			if (queen.hasFlower(housing)) {
				housing.setErrorState(EnumErrorCode.OK);
				doQueenWorkCycle();
			} else {
				housing.setErrorState(EnumErrorCode.NOFLOWER);
			}
		}
	}
	
	private void doQueenWorkCycle() {
		doProduction();
		queen.plantFlowerRandom(housing);
		doPollination();
		// Age the queen
		queen.age(housing.getWorld(), housing.getLifespanModifier(queen.getGenome(), queen.getMate(), 0f));
		updateQueenItemNBT();
	}

	private void doProduction() {
		// Produce and add stacks
		ItemStack[] products = queen.produceStacks(housing);
		housing.wearOutEquipment(1);
		for (ItemStack stack : products) {
			housing.addProduct(stack, false);
		}
	}

	private void doPollination() {
		// Get pollen if none available yet
		if (pollen == null) {
			pollen = queen.retrievePollen(housing);
			attemptedPollinations = 0;
			if (pollen != null) {
				if (housing.onPollenRetrieved(queen, pollen, false)) {
					pollen = null;
				}
			}
		}
		if (pollen != null) {
			attemptedPollinations++;
			if (queen.pollinateRandom(housing, pollen) || attemptedPollinations >= MAX_POLLINATION_ATTEMPTS) {
				pollen = null;
			}
		}
	}

	private void updateQueenItemNBT() {
		// Write the changed queen back into the item stack.
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		queen.writeToNBT(nbttagcompound);
		housing.getQueen().setTagCompound(nbttagcompound);
	}

	private boolean addPendingProducts() {
		EnumErrorCode housingErrorState = null;

		while (!spawn.isEmpty()) {
			ItemStack next = spawn.peek();
			if (housing.addProduct(next, true)) {
				spawn.pop();
				housingErrorState = EnumErrorCode.OK;
			} else {
				housingErrorState = EnumErrorCode.NOSPACE;
				break;
			}
		}

		if (housingErrorState != null) {
			housing.setErrorState(housingErrorState);
		}

		return housingErrorState != EnumErrorCode.NOSPACE;
	}
	
	private boolean hasHealthyQueen() {
		boolean hasQueen = true;
		EnumErrorCode housingErrorState = null;

		if (housing.getQueen() == null || !ForestryItem.beeQueenGE.isItemEqual(housing.getQueen())) {
			housingErrorState = EnumErrorCode.NOQUEEN;
			hasQueen = false;
		} else {
			IBee queen = BeeManager.beeRoot.getMember(housing.getQueen());
			// Kill dying queens
			if (!queen.isAlive()) {
				killQueen(queen);
				housingErrorState = EnumErrorCode.OK;
				hasQueen = false;
			} else {
				this.queen = queen;
			}
		}
		if (housingErrorState != null) {
			housing.setErrorState(housingErrorState);
		}
		return hasQueen;
	}
	
	private boolean tryBreedingPrincess() {
		boolean isBreedingPrincess = false;

		// Princess available? Try to breed!
		if (ForestryItem.beePrincessGE.isItemEqual(housing.getQueen())) {
			if (ForestryItem.beeDroneGE.isItemEqual(housing.getDrone())) {
				housing.setErrorState(EnumErrorCode.OK);
			} else {
				housing.setErrorState(EnumErrorCode.NODRONE);
			}
			tickBreed();
			isBreedingPrincess = true;
		}
		
		return isBreedingPrincess;
	}

	private boolean queenCanWork() {
		IErrorState state = queen.canWork(housing);
		if (state != EnumErrorCode.OK) {
			housing.setErrorState(state);
			return false;
		}

		if (housing.getErrorState() != EnumErrorCode.NOFLOWER) {
			housing.setErrorState(EnumErrorCode.OK);
		}
		return true;
	}

	// / BREEDING
	private void tickBreed() {
		if (!tryBreed()) {
			breedingTime = 0;
			return;
		}

		if (breedingTime < totalBreedingTime) {
			breedingTime++;
		}
		if (breedingTime < totalBreedingTime) {
			return;
		}

		// Breeding done, create new queen if slot available
		if (!ForestryItem.beePrincessGE.isItemEqual(housing.getQueen())) {
			return;
		}

		// Replace
		IBee princess = BeeManager.beeRoot.getMember(housing.getQueen());
		IBee drone = BeeManager.beeRoot.getMember(housing.getDrone());
		princess.mate(drone);

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		princess.writeToNBT(nbttagcompound);
		ItemStack queen = ForestryItem.beeQueenGE.getItemStack();
		queen.setTagCompound(nbttagcompound);

		housing.setQueen(queen);
		housing.onQueenChange(housing.getQueen());

		// Register the new queen with the breeding tracker
		BeeManager.beeRoot.getBreedingTracker(housing.getWorld(), housing.getOwnerName()).registerQueen(princess);

		// Remove drone
		housing.getDrone().stackSize--;
		if (housing.getDrone().stackSize <= 0) {
			housing.setDrone(null);
		}

		// Reset breeding time
		breedingTime = 0;
	}

	private boolean tryBreed() {
		if (housing.getDrone() == null || housing.getQueen() == null) {
			return false;
		}

		if (!ForestryItem.beeDroneGE.isItemEqual(housing.getDrone()) || !ForestryItem.beePrincessGE.isItemEqual(housing.getQueen())) {
			return false;
		}

		if (!housing.canBreed()) {
			return false;
		}

		return true;
	}

	private void killQueen(IBee queen) {
		if (queen.canSpawn()) {
			spawnOffspring(queen);
			housing.getQueen().stackSize = 0;
			housing.setQueen(null);
		} else {
			Proxies.log.warning("Tried to spawn offspring off an unmated queen. Devolving her to a princess.");

			ItemStack convert = ForestryItem.beePrincessGE.getItemStack();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			queen.writeToNBT(nbttagcompound);
			convert.setTagCompound(nbttagcompound);

			spawn.add(convert);
			housing.setQueen(null);
		}
		housing.onQueenChange(housing.getQueen());
	}

	/**
	 * Creates the succeeding princess and between one and three drones.
	 */
	private void spawnOffspring(IBee queen) {

		Stack<ItemStack> offspring = new Stack<ItemStack>();
		IApiaristTracker breedingTracker = BeeManager.beeRoot.getBreedingTracker(housing.getWorld(), housing.getOwnerName());

		housing.onQueenDeath(getQueen());

		// Princess
		boolean secondPrincess = this.housing.getWorld().rand.nextInt(10000) < PluginApiculture.getSecondPrincessChance() * 100;
		int count = secondPrincess ? 2 : 1;
		while (count > 0) {
			count--;
			IBee heiress = queen.spawnPrincess(housing);
			if (heiress != null) {
				ItemStack princess = BeeManager.beeRoot.getMemberStack(heiress, EnumBeeType.PRINCESS.ordinal());
				breedingTracker.registerPrincess(heiress);
				offspring.push(princess);
			}
		}

		// Drones
		IBee[] larvae = queen.spawnDrones(housing);
		for (IBee larva : larvae) {
			ItemStack drone = BeeManager.beeRoot.getMemberStack(larva, EnumBeeType.DRONE.ordinal());
			breedingTracker.registerDrone(larva);
			offspring.push(drone);
		}

		while (!offspring.isEmpty()) {
			ItemStack spawned = offspring.pop();
			if (!housing.addProduct(spawned, true)) {
				spawn.add(spawned);
			}
		}

		housing.onPostQueenDeath(getQueen());

	}
}
