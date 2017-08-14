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

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividualLiving;

public abstract class IndividualLiving extends Individual implements IIndividualLiving {

	private int health;
	private int maxHealth;

	protected IndividualLiving() {
	}

	protected IndividualLiving(int newHealth) {
		health = maxHealth = newHealth;
	}

	protected IndividualLiving(NBTTagCompound nbt) {
		super(nbt);
		health = nbt.getInteger("Health");
		maxHealth = nbt.getInteger("MaxH");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound = super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Health", health);
		nbttagcompound.setInteger("MaxH", maxHealth);

		NBTTagCompound nbtGenome = new NBTTagCompound();
		getGenome().writeToNBT(nbtGenome);
		nbttagcompound.setTag("Genome", nbtGenome);

		if (getMate() != null) {
			NBTTagCompound nbtMate = new NBTTagCompound();
			getMate().writeToNBT(nbtMate);
			nbttagcompound.setTag("Mate", nbtMate);
		}
		return nbttagcompound;
	}

	/* GENERATION */
	@Override
	@Nullable
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
	public final void setHealth(int health) {
		if (health < 0) {
			health = 0;
		} else if (health > getMaxHealth()) {
			health = getMaxHealth();
		}

		this.health = health;
	}

	@Override
	public int getMaxHealth() {
		return this.maxHealth;
	}

	@Override
	public void age(World world, float lifespanModifier) {

		if (lifespanModifier < 0.001f) {
			setHealth(0);
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

	private void decreaseHealth() {
		if (health > 0) {
			setHealth(health - 1);
		}
	}

}
