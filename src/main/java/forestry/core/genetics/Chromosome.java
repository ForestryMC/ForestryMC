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

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.core.utils.Log;

public class Chromosome implements IChromosome {

	private static final String UID0_TAG = "UID0";
	private static final String UID1_TAG = "UID1";

	private IAllele primary;
	private IAllele secondary;

	// / CONSTRUCTOR
	private Chromosome() {
	}

	public Chromosome(IAllele allele) {
		primary = secondary = allele;
	}

	public Chromosome(IAllele primary, IAllele secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		primary = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString(UID0_TAG));
		secondary = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString(UID1_TAG));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString(UID0_TAG, primary.getUID());
		nbttagcompound.setString(UID1_TAG, secondary.getUID());
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

	public boolean overrideInvalidAlleles(IAllele template, Class<? extends IAllele> chromosomeClass) {
		boolean foundInvalidAlleles = false;

		// use the other chromosome instead of the template if it's valid
		if (primary != null && chromosomeClass.isInstance(primary)) {
			template = primary;
		} else if (secondary != null && chromosomeClass.isInstance(secondary)) {
			template = secondary;
		}

		if (primary == null) {
			Log.warning("Missing primary allele: {0}. Setting to: {1}", this, template);
			primary = template;
			foundInvalidAlleles = true;
		} else if (!chromosomeClass.isInstance(primary)) {
			Log.warning("Wrong primary allele: {0}. Setting to: {1}", this, template);
			primary = template;
			foundInvalidAlleles = true;
		}
		
		if (secondary == null) {
			Log.warning("Missing secondary allele: {0}. Setting to: {1}", this, template);
			secondary = template;
			foundInvalidAlleles = true;
		} else if (!chromosomeClass.isInstance(secondary)) {
			Log.warning("Wrong secondary allele: {0}. Setting to: {1}", this, template);
			secondary = template;
			foundInvalidAlleles = true;
		}

		return foundInvalidAlleles;
	}
	
	public boolean hasInvalidAlleles(Class<? extends IAllele> chromosomeClass) {
		if (primary == null) {
			Log.severe("Missing primary allele: {0}", this);
			return true;
		}

		if (!chromosomeClass.isInstance(primary)) {
			Log.severe("Wrong primary allele for: {0}. Should be: {1}", this, chromosomeClass.getSimpleName());
			return true;
		}
		
		if (secondary == null) {
			Log.severe("Missing secondary allele: {0}", this);
			return true;
		}

		if (!chromosomeClass.isInstance(secondary)) {
			Log.severe("Wrong secondary allele for: {0}. Should be: {1}", this, chromosomeClass.getSimpleName());
			return true;
		}
		
		return false;
	}

	/* HELPER FUNCTIONS */
	public static Chromosome loadChromosomeFromNBT(NBTTagCompound compound) {
		Chromosome chromosome = new Chromosome();
		chromosome.readFromNBT(compound);
		return chromosome;
	}

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
