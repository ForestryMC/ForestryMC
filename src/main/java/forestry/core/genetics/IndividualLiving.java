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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import genetics.api.individual.IGenome;
import genetics.api.individual.Individual;

import forestry.api.genetics.IIndividualLiving;

public abstract class IndividualLiving extends Individual implements IIndividualLiving {
	private static final String NBT_HEALTH = "Health";
	private static final String NBT_MAX_HEALTH = "MaxH";

	private int health;
	private int maxHealth;

	protected IndividualLiving(IGenome genome, @Nullable IGenome mate) {
		super(genome, mate);
	}

	protected IndividualLiving(IGenome genome, @Nullable IGenome mate, int newHealth) {
		super(genome, mate);
		health = maxHealth = newHealth;
	}

	protected IndividualLiving(CompoundNBT nbt) {
		super(nbt);
		health = nbt.getInt(NBT_HEALTH);
		maxHealth = nbt.getInt(NBT_MAX_HEALTH);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);

		compound.putInt(NBT_HEALTH, health);
		compound.putInt(NBT_MAX_HEALTH, maxHealth);

		return compound;
	}

	/* GENERATION */
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
