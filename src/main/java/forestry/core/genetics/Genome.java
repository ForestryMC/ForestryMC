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

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.utils.Log;

public abstract class Genome implements IGenome {
	public static final String GENOME_TAG = "Genome";

	private final IChromosome[] chromosomes;

	protected Genome(NBTTagCompound nbttagcompound) {
		this.chromosomes = GenomeSaveHandler.readTag(getSpeciesRoot(), nbttagcompound);
	}

	protected Genome(IChromosome[] chromosomes) {
		checkChromosomes(chromosomes);
		this.chromosomes = chromosomes;
	}

	private void checkChromosomes(IChromosome[] chromosomes) {
		if (chromosomes.length != getDefaultTemplate().length) {
			String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template.\n%s", getSpeciesRoot().getUID(), chromosomesToString(chromosomes));
			throw new IllegalArgumentException(message);
		}

		IChromosomeType[] karyotype = getSpeciesRoot().getKaryotype();
		for (int i = 0; i < karyotype.length; i++) {
			IChromosomeType chromosomeType = karyotype[i];
			IChromosome chromosome = chromosomes[i];
			if (chromosome == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
						"Missing chromosome '%s'.\n%s", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			IAllele primary = chromosome.getActiveAllele();
			if (primary == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
						"Missing active allele for '%s'.\n%s", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			IAllele secondary = chromosome.getInactiveAllele();
			if (secondary == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
						"Missing inactive allele for '%s'.\n%s", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			Class<? extends IAllele> chromosomeAlleleClass = chromosomeType.getAlleleClass();
			if (!chromosomeAlleleClass.isAssignableFrom(primary.getClass())) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
						"Incorrect type for primary allele '%s'.\n%s.", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			if (!chromosomeAlleleClass.isAssignableFrom(secondary.getClass())) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
						"Incorrect type for secondary allele '%s'.\n%s.", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}
		}
	}

	private String chromosomesToString(IChromosome[] chromosomes) {
		StringBuilder stringBuilder = new StringBuilder();
		IChromosomeType[] karyotype = getSpeciesRoot().getKaryotype();
		for (int i = 0; i < chromosomes.length; i++) {
			IChromosomeType chromosomeType = karyotype[i];
			IChromosome chromosome = chromosomes[i];
			stringBuilder.append(chromosomeType.getName()).append(": ").append(chromosome).append("\n");
		}

		return stringBuilder.toString();
	}

	private IAllele[] getDefaultTemplate() {
		return getSpeciesRoot().getDefaultTemplate();
	}

	// NBT RETRIEVAL

	/**
	 * Quickly gets the species without loading the whole genome.
	 * We need this because the client uses the species for rendering.
	 */
	@Nullable
	public static IAlleleSpecies getSpeciesDirectly(ISpeciesRoot speciesRoot, ItemStack itemStack) {
		return (IAlleleSpecies) getAlleleDirectly(speciesRoot.getSpeciesChromosomeType(), true, itemStack);
	}

	/**
	 * Quickly gets the species without loading the whole genome. And without creating absent chromosomes.
	 */
	@Nullable
	public static IAllele getAlleleDirectly(IChromosomeType chromosomeType, boolean active, ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genomeNBT = nbtTagCompound.getCompoundTag(GENOME_TAG);
		if (genomeNBT.hasNoTags()) {
			return null;
		}
		IAllele allele = GenomeSaveHandler.getAlleleDirectly(genomeNBT, chromosomeType, active);
		if(!chromosomeType.getAlleleClass().isInstance(allele)){
			return null;
		}
		return allele;
	}

	public static IAllele getAllele(ItemStack itemStack, IChromosomeType chromosomeType, boolean active) {
		IChromosome chromosome = getSpecificChromosome(itemStack, chromosomeType);
		return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
	}

	/**
	 * Tries to load a specific chromosome and creates it if it is absent.
	 */
	private static IChromosome getSpecificChromosome(ItemStack itemStack, IChromosomeType chromosomeType) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			nbtTagCompound = new NBTTagCompound();
			itemStack.setTagCompound(nbtTagCompound);
		}
		NBTTagCompound genomeNBT = nbtTagCompound.getCompoundTag(GENOME_TAG);

		if (genomeNBT.hasNoTags()) {
			Log.error("Got a genetic item with no genome, setting it to a default value.");
			genomeNBT = new NBTTagCompound();

			ISpeciesRoot speciesRoot = chromosomeType.getSpeciesRoot();
			IAllele[] defaultTemplate = speciesRoot.getDefaultTemplate();
			IGenome genome = speciesRoot.templateAsGenome(defaultTemplate);
			genome.writeToNBT(genomeNBT);
			nbtTagCompound.setTag(GENOME_TAG, genomeNBT);
		}

		return GenomeSaveHandler.getSpecificChromosome(genomeNBT, chromosomeType);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		return GenomeSaveHandler.writeTag(chromosomes, getSpeciesRoot(), tagCompound);
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

			if (!chromosome.getActiveAllele().getUID().equals(otherChromosome.getActiveAllele().getUID())) {
				return false;
			}
			if (!chromosome.getInactiveAllele().getUID().equals(otherChromosome.getInactiveAllele().getUID())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
		int i = 0;
		for (IChromosome chromosome : chromosomes) {
			toStringHelper.add(String.valueOf(i++), chromosome);
		}
		return toStringHelper.toString();
	}


}
