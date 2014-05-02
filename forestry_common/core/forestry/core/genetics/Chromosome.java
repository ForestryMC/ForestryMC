/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.ILegacyHandler;

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

		// Legacy
		if (nbttagcompound.hasKey("PrimaryId")) {
			primary = ((ILegacyHandler) AlleleManager.alleleRegistry).getFromLegacyMap(nbttagcompound.getInteger("PrimaryId"));
			secondary = ((ILegacyHandler) AlleleManager.alleleRegistry).getFromLegacyMap(nbttagcompound.getInteger("SecondaryId"));

			if (primary == null || secondary == null)
				throw new RuntimeException("Legacy conversion of chromosome failed. Did one of your bee addons not update? No legacy mapping for ids: "
						+ nbttagcompound.getInteger("PrimaryId") + " - " + nbttagcompound.getInteger("SecondaryId"));

			return;
		}

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
		if (primary.isDominant())
			return primary;
		if (secondary.isDominant())
			return secondary;
		// Leaves only the case of both being recessive
		return primary;
	}

	@Override
	public IAllele getInactiveAllele() {
		if (!secondary.isDominant())
			return secondary;
		if (!primary.isDominant())
			return primary;
		// Leaves only the case of both being dominant
		return secondary;
	}

	public IAllele getRandomAllele(Random rand) {
		if (rand.nextBoolean())
			return primary;
		else
			return secondary;
	}

	public void overrideInvalidAlleles(IAllele template, Class<? extends IAllele> chromosomeClass) {
		if (primary == null || !chromosomeClass.isInstance(primary))
			primary = template;
		if (secondary == null || !chromosomeClass.isInstance(secondary))
			secondary = template;
	}

	/* HELPER FUNCTIONS */
	public static Chromosome loadChromosomeFromNBT(NBTTagCompound compound) {
		Chromosome chromosome = new Chromosome();
		chromosome.readFromNBT(compound);
		return chromosome;
	}

	public static IChromosome inheritChromosome(Random rand, IChromosome parent1, IChromosome parent2) {

		IAllele choice1;
		if (rand.nextBoolean())
			choice1 = parent1.getPrimaryAllele();
		else
			choice1 = parent1.getSecondaryAllele();

		IAllele choice2;
		if (rand.nextBoolean())
			choice2 = parent2.getPrimaryAllele();
		else
			choice2 = parent2.getSecondaryAllele();

		if (rand.nextBoolean())
			return new Chromosome(choice1, choice2);
		else
			return new Chromosome(choice2, choice1);
	}

}
