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

import javax.annotation.Nonnull;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.core.utils.Log;

public class Chromosome implements IChromosome {

	private static final String UID0_TAG = "UID0";
	private static final String UID1_TAG = "UID1";

	@Nonnull
	private final IAllele primary;
	@Nonnull
	private final IAllele secondary;

	// / CONSTRUCTOR
	public Chromosome(@Nonnull IAllele allele) {
		primary = secondary = allele;
	}

	public Chromosome(@Nonnull IAllele primary, @Nonnull IAllele secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	public Chromosome(@Nonnull NBTTagCompound nbt) {
		primary = AlleleManager.alleleRegistry.getAllele(nbt.getString(UID0_TAG));
		secondary = AlleleManager.alleleRegistry.getAllele(nbt.getString(UID1_TAG));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString(UID0_TAG, primary.getUID());
		nbttagcompound.setString(UID1_TAG, secondary.getUID());
		return nbttagcompound;
	}

	@Override
	public IAllele getPrimaryAllele() {
		return primary;
	}

	@Override
	public IAllele getSecondaryAllele() {
		return secondary;
	}

	@Override
	public IAllele getActiveAllele() {
		if (primary == null || secondary == null) {
			return null;
		}
		if (primary.isDominant()) {
			return primary;
		}
		if (secondary.isDominant()) {
			return secondary;
		}
		// Leaves only the case of both being recessive
		return primary;
	}

	@Override
	public IAllele getInactiveAllele() {
		if (!secondary.isDominant()) {
			return secondary;
		}
		if (!primary.isDominant()) {
			return primary;
		}
		// Leaves only the case of both being dominant
		return secondary;
	}

	public IAllele getRandomAllele(Random rand) {
		if (rand.nextBoolean()) {
			return primary;
		} else {
			return secondary;
		}
	}
	
	public boolean hasInvalidAlleles(Class<? extends IAllele> chromosomeClass) {
		if (primary == null) {
			Log.warning("Missing primary allele: {}", this);
			return true;
		}

		if (!chromosomeClass.isInstance(primary)) {
			Log.warning("Wrong primary allele for: {}. Should be: {}", this, chromosomeClass.getSimpleName());
			return true;
		}
		
		if (secondary == null) {
			Log.warning("Missing secondary allele: {}", this);
			return true;
		}

		if (!chromosomeClass.isInstance(secondary)) {
			Log.warning("Wrong secondary allele for: {}. Should be: {}", this, chromosomeClass.getSimpleName());
			return true;
		}
		
		return false;
	}

	/* HELPER FUNCTIONS */
	public static IChromosome inheritChromosome(Random rand, IChromosome parent1, IChromosome parent2) {

		IAllele choice1;
		if (rand.nextBoolean()) {
			choice1 = parent1.getPrimaryAllele();
		} else {
			choice1 = parent1.getSecondaryAllele();
		}

		IAllele choice2;
		if (rand.nextBoolean()) {
			choice2 = parent2.getPrimaryAllele();
		} else {
			choice2 = parent2.getSecondaryAllele();
		}

		if (rand.nextBoolean()) {
			return new Chromosome(choice1, choice2);
		} else {
			return new Chromosome(choice2, choice1);
		}
	}

	@Override
	public String toString() {
		return "{" + primary + ", " + secondary + "}";
	}

}
