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

import com.google.common.base.Objects;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Config;

public abstract class Genome implements IGenome {

	private static final String SLOT_TAG = "Slot";

	private IChromosome[] chromosomes;

	// / CONSTRUCTOR
	public Genome(NBTTagCompound nbttagcompound) {
		this.chromosomes = new Chromosome[getDefaultTemplate().length];
		readFromNBT(nbttagcompound);
	}

	private IAllele[] getDefaultTemplate() {
		return getSpeciesRoot().getDefaultTemplate();
	}

	public Genome(IChromosome[] chromosomes) {
		if (chromosomes.length != getDefaultTemplate().length) {
			throw new IllegalArgumentException(String.format("Tried to create a genome for '%s' from an invalid chromosome template.", getSpeciesRoot().getUID()));
		}
		this.chromosomes = chromosomes;
	}

	// NBT RETRIEVAL

	/**
	 * Quickly gets the species without loading the whole genome.
	 * We need this because the client uses the species for rendering.
	 */
	protected static IAlleleSpecies getSpeciesDirectly(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genomeNBT = nbtTagCompound.getCompoundTag("Genome");
		if (genomeNBT == null) {
			return null;
		}

		NBTTagList chromosomesNBT = genomeNBT.getTagList("Chromosomes", 10);
		if (chromosomesNBT == null) {
			return null;
		}

		NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(0);
		Chromosome chromosome = Chromosome.loadChromosomeFromNBT(chromosomeNBT);

		IAllele activeAllele = chromosome.getActiveAllele();
		if (!(activeAllele instanceof IAlleleSpecies)) {
			return null;
		}

		return (IAlleleSpecies) activeAllele;
	}

	public static IChromosome getChromosome(ItemStack itemStack, IChromosomeType chromosomeType, ISpeciesRoot speciesRoot) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genome = nbtTagCompound.getCompoundTag("Genome");
		if (genome == null) {
			return null;
		}

		IChromosome[] chromosomes = getChromosomes(genome, speciesRoot);
		if (chromosomes == null) {
			return null;
		}

		return chromosomes[chromosomeType.ordinal()];
	}

	public static IChromosome[] getChromosomes(NBTTagCompound genomeNBT, ISpeciesRoot speciesRoot) {

		NBTTagList chromosomesNBT = genomeNBT.getTagList("Chromosomes", 10);
		IChromosome[] chromosomes = new IChromosome[speciesRoot.getDefaultTemplate().length];

		for (int i = 0; i < chromosomesNBT.tagCount(); i++) {
			NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(i);
			byte chromosomeOrdinal = chromosomeNBT.getByte(SLOT_TAG);

			if (chromosomeOrdinal >= 0 && chromosomeOrdinal < chromosomes.length) {
				Chromosome chromosome = Chromosome.loadChromosomeFromNBT(chromosomeNBT);
				chromosomes[chromosomeOrdinal] = chromosome;

				if (Config.clearInvalidChromosomes) {
					IAllele template = speciesRoot.getDefaultTemplate()[chromosomeOrdinal];
					Class<? extends IAllele> chromosomeClass = speciesRoot.getKaryotype()[chromosomeOrdinal].getAlleleClass();
					if (chromosome.overrideInvalidAlleles(template, chromosomeClass)) {
						chromosome.writeToNBT(chromosomeNBT);
					}
				}

				if (chromosome.hasInvalidAlleles(speciesRoot.getKaryotype()[chromosomeOrdinal].getAlleleClass())) {
					throw new RuntimeException("Found Chromosome with invalid Alleles.\nNBTTagCompound: " + chromosomesNBT + "\nSee config option \"genetics.clear.invalid.chromosomes\".\nMissing: " + chromosomeNBT);
				}
			}
		}

		// handle old saves that have missing chromosomes
		IChromosome speciesChromosome = chromosomes[EnumTreeChromosome.SPECIES.ordinal()];
		if (speciesChromosome != null) {
			IAlleleSpecies species = (IAlleleSpecies) speciesChromosome.getActiveAllele();
			IAllele[] template = speciesRoot.getTemplate(species.getUID());
			for (int i = 0; i < chromosomes.length; i++) {
				IAllele allele = template[i];
				if ((chromosomes[i] == null) && (allele != null)) {
					chromosomes[i] = new Chromosome(allele);
				}
			}
		}

		return chromosomes;
	}

	public static IAllele getActiveAllele(ItemStack itemStack, IChromosomeType chromosomeType, ISpeciesRoot speciesRoot) {
		IChromosome chromosome = getChromosome(itemStack, chromosomeType, speciesRoot);
		if (chromosome == null) {
			return null;
		}
		return chromosome.getActiveAllele();
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		chromosomes = getChromosomes(nbttagcompound, getSpeciesRoot());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < chromosomes.length; i++) {
			if (chromosomes[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte(SLOT_TAG, (byte) i);
				chromosomes[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("Chromosomes", nbttaglist);
	}

	// / INFORMATION RETRIEVAL
	@Override
	public IChromosome[] getChromosomes() {
		return Arrays.copyOf(chromosomes, chromosomes.length);
	}

	@Override
	public IAllele getActiveAllele(IChromosomeType chromosomeType) {
		return chromosomes[chromosomeType.ordinal()].getActiveAllele();
	}

	@Override
	public IAllele getInactiveAllele(IChromosomeType chromosomeType) {
		return chromosomes[chromosomeType.ordinal()].getInactiveAllele();
	}

	@Override
	public boolean isGeneticEqual(IGenome other) {
		IChromosome[] genetics = other.getChromosomes();
		if (chromosomes.length != genetics.length) {
			return false;
		}

		for (int i = 0; i < chromosomes.length; i++) {
			IChromosome chromosome = chromosomes[i];
			IChromosome otherChromosome = genetics[i];
			if (chromosome == null && otherChromosome == null) {
				continue;
			}
			if (chromosome == null || otherChromosome == null) {
				return false;
			}

			if (!chromosome.getPrimaryAllele().getUID().equals(otherChromosome.getPrimaryAllele().getUID())) {
				return false;
			}
			if (!chromosome.getSecondaryAllele().getUID().equals(otherChromosome.getSecondaryAllele().getUID())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		Objects.ToStringHelper toStringHelper = Objects.toStringHelper(this);
		int i = 0;
		for (IChromosome chromosome : chromosomes) {
			toStringHelper.add(String.valueOf(i++), chromosome);
		}
		return toStringHelper.toString();
	}
}
