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
import net.minecraft.nbt.NBTTagList;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.utils.Log;

public abstract class Genome implements IGenome {
	private static final String SLOT_TAG = "Slot";

	private final IChromosome[] chromosomes;

	protected Genome(NBTTagCompound nbttagcompound) {
		this.chromosomes = getChromosomes(nbttagcompound, getSpeciesRoot());
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

			IAllele primary = chromosome.getPrimaryAllele();
			if (primary == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Missing primary allele for '%s'.\n%s", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			IAllele secondary = chromosome.getSecondaryAllele();
			if (secondary == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Missing secondary allele for '%s'.\n%s", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
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
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genomeNBT = nbtTagCompound.getCompoundTag("Genome");
		if (genomeNBT.isEmpty()) {
			return null;
		}

		NBTTagList chromosomesNBT = genomeNBT.getTagList("Chromosomes", 10);
		if (chromosomesNBT.isEmpty()) {
			return null;
		}

		NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(0);
		Chromosome chromosome = Chromosome.create(null, null, speciesRoot.getSpeciesChromosomeType(), chromosomeNBT);

		IAllele activeAllele = chromosome.getActiveAllele();
		if (!(activeAllele instanceof IAlleleSpecies)) {
			return null;
		}

		return (IAlleleSpecies) activeAllele;
	}

	/**
	 * Quickly gets the species without loading the whole genome.
	 */
	@Nullable
	public static IAllele getSpeciesDirectly(ItemStack itemStack, IChromosomeType chromosomeType, boolean active) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genomeNBT = nbtTagCompound.getCompoundTag("Genome");
		if (genomeNBT.isEmpty()) {
			return null;
		}

		NBTTagList chromosomesNBT = genomeNBT.getTagList("Chromosomes", 10);
		if (chromosomesNBT.isEmpty()) {
			return null;
		}

		NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(0);
		Chromosome chromosome = Chromosome.create(null, null, chromosomeType, chromosomeNBT);

		return active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
	}

	private static IChromosome getChromosome(ItemStack itemStack, IChromosomeType chromosomeType, ISpeciesRoot speciesRoot) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			nbtTagCompound = new NBTTagCompound();
			itemStack.setTagCompound(nbtTagCompound);
		}

		NBTTagCompound genomeNbt = nbtTagCompound.getCompoundTag("Genome");
		if (genomeNbt.isEmpty()) {
			Log.error("Got a genetic item with no genome, setting it to a default value.");
			genomeNbt = new NBTTagCompound();


			IAllele[] defaultTemplate = speciesRoot.getDefaultTemplate();
			IGenome genome = speciesRoot.templateAsGenome(defaultTemplate);
			genome.writeToNBT(genomeNbt);
			nbtTagCompound.setTag("Genome", genomeNbt);
		}

		IChromosome[] chromosomes = getChromosomes(genomeNbt, speciesRoot);

		return chromosomes[chromosomeType.ordinal()];
	}


	private static IChromosome[] getChromosomes(NBTTagCompound genomeNBT, ISpeciesRoot speciesRoot) {
		NBTTagList chromosomesNBT = genomeNBT.getTagList("Chromosomes", 10);
		IChromosome[] chromosomes = new IChromosome[speciesRoot.getDefaultTemplate().length];

		String primarySpeciesUid = null;
		String secondarySpeciesUid = null;

		for (int i = 0; i < chromosomesNBT.tagCount(); i++) {
			NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(i);
			byte chromosomeOrdinal = chromosomeNBT.getByte(SLOT_TAG);

			if (chromosomeOrdinal >= 0 && chromosomeOrdinal < chromosomes.length) {
				IChromosomeType chromosomeType = speciesRoot.getKaryotype()[chromosomeOrdinal];
				Chromosome chromosome = Chromosome.create(primarySpeciesUid, secondarySpeciesUid, chromosomeType, chromosomeNBT);
				chromosomes[chromosomeOrdinal] = chromosome;

				if (chromosomeOrdinal == speciesRoot.getSpeciesChromosomeType().ordinal()) {
					primarySpeciesUid = chromosome.getPrimaryAllele().getUID();
					secondarySpeciesUid = chromosome.getSecondaryAllele().getUID();
				}
			}
		}
		return chromosomes;
	}

	public static IAllele getAllele(ItemStack itemStack, IChromosomeType type, boolean active) {
		IAllele allele = getSpeciesDirectly(itemStack, type, active);
		if (allele == null) {
			IChromosome chromosome = getChromosome(itemStack, type, type.getSpeciesRoot());
			allele = active ? chromosome.getActiveAllele() : chromosome.getInactiveAllele();
		}
		return allele;
	}

	@Nullable
	public static <A extends IAllele> A getAllele(ItemStack itemStack, IChromosomeType type, boolean active, Class<? extends A> alleleClass) {
		IAllele allele = getAllele(itemStack, type, active);
		if (alleleClass.isInstance(allele)) {
			return alleleClass.cast(allele);
		}
		return null;
	}

	protected static IAllele getActiveAllele(ItemStack itemStack, IChromosomeType chromosomeType, ISpeciesRoot speciesRoot) {
		IChromosome chromosome = getChromosome(itemStack, chromosomeType, speciesRoot);
		return chromosome.getActiveAllele();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
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
		return nbttagcompound;
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
		MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
		int i = 0;
		for (IChromosome chromosome : chromosomes) {
			toStringHelper.add(String.valueOf(i++), chromosome);
		}
		return toStringHelper.toString();
	}
}
