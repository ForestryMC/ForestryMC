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
package forestry.apiculture.genetics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.core.genetics.BreedingTracker;

public class ApiaristTracker extends BreedingTracker implements IApiaristTracker {

	/** Required for creation from map storage */
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
		if (!individual.getGenome().getPrimary().getRoot().getUID().equals(BeeHelper.UID)) {
			return;
		}
		if (!individual.isPureBred(EnumBeeChromosome.SPECIES)) {
			return;
		}
		if (BeeManager.beeRoot.getCombinations(individual.getGenome().getPrimary()).size() > 0) {
			return;
		}

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
	protected IBreedingTracker getBreedingTracker(EntityPlayer player) {
		return BeeManager.beeRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	@Override
	protected String speciesRootUID() {
		return BeeHelper.UID;
	}

}
