/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.core.genetics.BreedingTracker;
import forestry.plugins.PluginApiculture;

public class ApiaristTracker extends BreedingTracker implements IApiaristTracker {

	public ApiaristTracker(String s) {
		this(s, null);
	}

	public ApiaristTracker(String s, GameProfile player) {
		super(s, player);
	}

	private int queensTotal = 0;
	private int dronesTotal = 0;
	private int princessesTotal = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		queensTotal = nbttagcompound.getInteger("QueensTotal");
		princessesTotal = nbttagcompound.getInteger("PrincessesTotal");
		dronesTotal = nbttagcompound.getInteger("DronesTotal");

		super.readFromNBT(nbttagcompound);

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setInteger("QueensTotal", queensTotal);
		nbttagcompound.setInteger("PrincessesTotal", princessesTotal);
		nbttagcompound.setInteger("DronesTotal", dronesTotal);

		super.writeToNBT(nbttagcompound);

	}

	@Override
	public void registerPickup(IIndividual individual) {
		if(!individual.getGenome().getPrimary().getRoot().getUID().equals(BeeHelper.UID))
			return;
		if(!individual.isPureBred(0))
			return;
		if(PluginApiculture.beeInterface.getCombinations(individual.getGenome().getPrimary()).size() > 0)
			return;

		registerSpecies(individual.getGenome().getPrimary());
	}

	@Override
	public void registerQueen(IIndividual bee) {
		queensTotal++;
	}

	@Override
	public int getQueenCount() {
		return queensTotal;
	}

	@Override
	public void registerPrincess(IIndividual bee) {

		princessesTotal++;
		registerBirth(bee);
	}

	@Override
	public int getPrincessCount() {
		return princessesTotal;
	}

	@Override
	public void registerDrone(IIndividual bee) {
		dronesTotal++;
		registerBirth(bee);
	}

	@Override
	public int getDroneCount() {
		return dronesTotal;
	}

	@Override
	protected IBreedingTracker getCommonTracker(EntityPlayer player) {
		return PluginApiculture.beeInterface.getBreedingTracker(player.worldObj, null);
	}

	@Override
	protected String getPacketTag() {
		return BeeHelper.UID;
	}

}
