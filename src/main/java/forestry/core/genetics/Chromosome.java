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
	private static final String UID0_TAG = "UID0";
	private static final String UID1_TAG = "UID1";

	private final IAllele primary;
	private final IAllele secondary;

	public static Chromosome create(@Nullable String primarySpeciesUid, @Nullable String secondarySpeciesUid, IChromosomeType chromosomeType, NBTTagCompound nbt) {
		IAllele primary = AlleleManager.alleleRegistry.getAllele(nbt.getString(UID0_TAG));
		IAllele secondary = AlleleManager.alleleRegistry.getAllele(nbt.getString(UID1_TAG));

		primary = validateAllele(primarySpeciesUid, chromosomeType, primary);
		secondary = validateAllele(secondarySpeciesUid, chromosomeType, secondary);

		return new Chromosome(primary, secondary);
	}

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

	public Chromosome(IAllele allele) {
		primary = secondary = allele;
	}

	public Chromosome(IAllele primary, IAllele secondary) {
		this.primary = primary;
		this.secondary = secondary;
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
