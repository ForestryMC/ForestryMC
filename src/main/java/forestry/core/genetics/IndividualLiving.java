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
package forestry.core.genetics;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividualLiving;

public abstract class IndividualLiving extends Individual implements IIndividualLiving {

	protected int health;
	protected int maxHealth;

	public IndividualLiving() {
	}
	
	public IndividualLiving(int newHealth) {
		health = maxHealth = newHealth;
	}
	
	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		health = nbttagcompound.getInteger("Health");
		maxHealth = nbttagcompound.getInteger("MaxH");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Health", health);
		nbttagcompound.setInteger("MaxH", maxHealth);

		if (getGenome() != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			getGenome().writeToNBT(NBTmachine);
			nbttagcompound.setTag("Genome", NBTmachine);
		}
		if (getMate() != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			getMate().writeToNBT(NBTmachine);
			nbttagcompound.setTag("Mate", NBTmachine);
		}

	}

	/* GENERATION */
	public abstract IGenome getMate();

	@Override
	public boolean isAlive() {
		return health > 0;
	}

	@Override
	public int getHealth() {
		return health;
	}

	@Override
	public int getMaxHealth() {
		return this.maxHealth;
	}

	@Override
	public void age(World world, float lifespanModifier) {

		if (lifespanModifier < 0.001f) {
			this.health = 0;
			return;
		}

		float ageModifier = 1.0f / lifespanModifier;

		while (ageModifier > 1.0f) {
			decreaseHealth();
			ageModifier--;
		}
		if (world.rand.nextFloat() < ageModifier) {
			decreaseHealth();
		}
	}

	public void decreaseHealth() {
		if (health > 0) {
			health--;
		}
	}

}
