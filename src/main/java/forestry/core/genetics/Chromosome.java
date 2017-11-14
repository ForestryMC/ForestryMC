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
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;

public class Chromosome implements IChromosome {
	private static final String ACTIVE_ALLELE_TAG = "UID0";
	private static final String INACTIVE_ALLELE_TAG = "UID1";

	public static Chromosome create(@Nullable String primarySpeciesUid, @Nullable String secondarySpeciesUid, IChromosomeType chromosomeType, NBTTagCompound nbt) {
		IAllele primary = AlleleManager.alleleRegistry.getAllele(nbt.getString(ACTIVE_ALLELE_TAG));
		IAllele secondary = AlleleManager.alleleRegistry.getAllele(nbt.getString(INACTIVE_ALLELE_TAG));
		return create(primarySpeciesUid, secondarySpeciesUid, chromosomeType, primary, secondary);
	}

	public static Chromosome create(@Nullable String primarySpeciesUid, @Nullable String secondarySpeciesUid, IChromosomeType chromosomeType, @Nullable IAllele primary, @Nullable IAllele secondary) {
		primary = validateAllele(primarySpeciesUid, chromosomeType, primary);
		secondary = validateAllele(secondarySpeciesUid, chromosomeType, secondary);

		return create(primary, secondary);
	}

	public static Chromosome create(IAllele allele){
		return new Chromosome(allele);
	}

	public static Chromosome create(IAllele firstAllele, IAllele secondAllele){
		firstAllele = getActiveAllele(firstAllele, secondAllele);
		secondAllele = getInactiveAllele(firstAllele, secondAllele);
		return new Chromosome(firstAllele, secondAllele);
	}

	private final IAllele active;
	private final IAllele inactive;

	private Chromosome(IAllele allele) {
		active = inactive = allele;
	}

	private Chromosome(IAllele active, IAllele inactive) {
		this.active = active;
		this.inactive = inactive;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString(ACTIVE_ALLELE_TAG, active.getUID());
		nbttagcompound.setString(INACTIVE_ALLELE_TAG, inactive.getUID());
		return nbttagcompound;
	}

	@Override
	@Deprecated
	public IAllele getPrimaryAllele() {
		return active;
	}

	@Override
	@Deprecated
	public IAllele getSecondaryAllele() {
		return inactive;
	}

	@Override
	public IAllele getActiveAllele() {
		return active;
	}

	@Override
	public IAllele getInactiveAllele() {
		return inactive;
	}

	@Override
	public IChromosome inheritChromosome(Random rand, IChromosome other) {
		IAllele firstChoice;
		if (rand.nextBoolean()) {
			firstChoice = getActiveAllele();
		} else {
			firstChoice = getInactiveAllele();
		}

		IAllele secondChoice;
		if (rand.nextBoolean()) {
			secondChoice = other.getActiveAllele();
		} else {
			secondChoice = other.getInactiveAllele();
		}

		if (rand.nextBoolean()) {
			return Chromosome.create(firstChoice, secondChoice);
		} else {
			return Chromosome.create(secondChoice, firstChoice);
		}
	}

	@Override
	public String toString() {
		return "{" + active + ", " + inactive + "}";
	}

	/* HELPER FUNCTIONS */
	@Deprecated
	public static IChromosome inheritChromosome(Random rand, IChromosome firstParent, IChromosome secondParent) {
		return firstParent.inheritChromosome(rand, secondParent);
	}

	@Nullable
	static IAllele getActiveAllele(NBTTagCompound chromosomeNBT){
		String alleleUid = chromosomeNBT.getString(Chromosome.ACTIVE_ALLELE_TAG);
		return AlleleManager.alleleRegistry.getAllele(alleleUid);
	}

	@Nullable
	static IAllele getInactiveAllele(NBTTagCompound chromosomeNBT){
		String alleleUid = chromosomeNBT.getString(Chromosome.ACTIVE_ALLELE_TAG);
		return AlleleManager.alleleRegistry.getAllele(alleleUid);
	}

	/**
	 * Checks if the allele is not null and valid for that type of chromosome.
	 */
	private static IAllele validateAllele(@Nullable String speciesUid, IChromosomeType chromosomeType, @Nullable IAllele allele) {
		if (!chromosomeType.getAlleleClass().isInstance(allele)) {
			ISpeciesRoot speciesRoot = chromosomeType.getSpeciesRoot();

			IAllele[] template = null;

			if (speciesUid != null) {
				template = speciesRoot.getTemplate(speciesUid);
			}

			if (template == null) {
				template = speciesRoot.getDefaultTemplate();
			}

			return template[chromosomeType.ordinal()];
		}
		return allele;
	}

	private static IAllele getActiveAllele(IAllele firstAllele, IAllele secondAllele) {
		if (firstAllele.isDominant()) {
			return firstAllele;
		}
		if (secondAllele.isDominant()) {
			return secondAllele;
		}
		// Leaves only the case of both being recessive
		return firstAllele;
	}

	private static IAllele getInactiveAllele(IAllele firstAllele, IAllele secondAllele) {
		if (!secondAllele.isDominant()) {
			return secondAllele;
		}
		if (!firstAllele.isDominant()) {
			return firstAllele;
		}
		// Leaves only the case of both being dominant
		return secondAllele;
	}

}
