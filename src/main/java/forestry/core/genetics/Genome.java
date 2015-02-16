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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.core.config.Config;
import forestry.core.proxy.Proxies;

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
	public static Chromosome getChromosome(ItemStack itemStack, IChromosomeType chromosomeType) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genome = nbtTagCompound.getCompoundTag("Genome");
		if (genome == null) {
			return null;
		}

		NBTTagList chromosomes = genome.getTagList("Chromosomes", 10);
		if (chromosomes == null) {
			return null;
		}

		for (int i = 0; i < chromosomes.tagCount(); i++) {
			NBTTagCompound chromosomeTag = chromosomes.getCompoundTagAt(i);
			byte byte0 = chromosomeTag.getByte(SLOT_TAG);

			if (byte0 == chromosomeType.ordinal()) {
				return Chromosome.loadChromosomeFromNBT(chromosomeTag);
			}
		}
		return null;
	}

	public static IAllele getActiveAllele(ItemStack itemStack, IChromosomeType chromosomeType) {
		Chromosome chromosome = getChromosome(itemStack, chromosomeType);
		if (chromosome == null) {
			return null;
		}
		return chromosome.getActiveAllele();
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = nbttagcompound.getTagList("Chromosomes", 10);
		chromosomes = new Chromosome[chromosomes.length];

		Boolean invalidGenome = false;

		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte(SLOT_TAG);
			
			if (byte0 >= 0 && byte0 < chromosomes.length) {
				Chromosome chromosome = Chromosome.loadChromosomeFromNBT(nbttagcompound1);
				chromosomes[byte0] = chromosome;

				if (Config.clearInvalidChromosomes) {
					if (chromosome.overrideInvalidAlleles(getDefaultTemplate()[byte0], getSpeciesRoot().getKaryotype()[byte0].getAlleleClass())) {
						invalidGenome = true;
					}
				}
				
				if (chromosome.hasInvalidAlleles(getSpeciesRoot().getKaryotype()[byte0].getAlleleClass())) {
					throw new RuntimeException("Found Chromosome with invalid Alleles.\nNBTTagCompound: " + nbttaglist + "\nSee config option \"genetics.clear.invalid.chromosomes\".");
				}
			}
		}

		if (invalidGenome) {
			Proxies.log.warning("Overrode alleles for genome:\n{0}\nOriginal NBTTagCompound: {1}", this, nbttaglist);
		}

		// handle old saves that have missing chromosomes
		IChromosome speciesChromosome = chromosomes[EnumTreeChromosome.SPECIES.ordinal()];
		if (speciesChromosome != null) {
			IAlleleSpecies species = (IAlleleSpecies) speciesChromosome.getActiveAllele();
			IAllele[] template = getSpeciesRoot().getTemplate(species.getUID());
			for (int i = 0; i < chromosomes.length; i++) {
				if ((chromosomes[i] == null) && (template[i] != null)) {
					chromosomes[i] = new Chromosome(template[i]);
				}
			}
		}
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
		return chromosomes;
	}

	@Override
	public IAllele getActiveAllele(int chromosome) {
		return chromosomes[chromosome].getActiveAllele();
	}

	@Override
	public IAllele getActiveAllele(IChromosomeType chromosomeType) {
		return chromosomes[chromosomeType.ordinal()].getActiveAllele();
	}

	@Override
	public IAllele getInactiveAllele(int chromosome) {
		return chromosomes[chromosome].getInactiveAllele();
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
