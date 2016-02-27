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
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Config;
import forestry.core.utils.Log;

public abstract class Genome<C extends Enum<C> & IChromosomeType<C>> implements IGenome<C> {

	private static final String UID_KEY = "uid";

	@Nonnull
	private final ImmutableMap<C, IChromosome> chromosomes;

	protected Genome(@Nonnull NBTTagCompound nbttagcompound) {
		this.chromosomes = getChromosomes(nbttagcompound, getSpeciesRoot());
	}

	protected Genome(@Nonnull ImmutableMap<C, IChromosome> chromosomes) {
		checkChromosomes(chromosomes);
		this.chromosomes = chromosomes;
	}

	private void checkChromosomes(ImmutableMap<C, IChromosome> chromosomes) {
		if (chromosomes == null || chromosomes.size() != getDefaultTemplate().size()) {
			String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template.\n%s", getSpeciesRoot().getUID(), chromosomesToString(chromosomes));
			throw new IllegalArgumentException(message);
		}

		C[] karyotype = getSpeciesRoot().getKaryotype();
		for (C chromosomeType : karyotype) {
			IChromosome chromosome = chromosomes.get(chromosomeType);
			if (chromosome == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
						"Missing chromosome '%s'.\n%s", getSpeciesRoot().getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			IAllele primary = chromosome.getPrimaryAllele();
			IAllele secondary = chromosome.getSecondaryAllele();

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

	@Nonnull
	private String chromosomesToString(@Nonnull ImmutableMap<C, IChromosome> chromosomes) {
		if (chromosomes == null) {
			return "null";
		}

		StringBuilder stringBuilder = new StringBuilder();
		C[] karyotype = getSpeciesRoot().getKaryotype();
		for (int i = 0; i < chromosomes.size(); i++) {
			C chromosomeType = karyotype[i];
			IChromosome chromosome = chromosomes.get(chromosomeType);
			stringBuilder.append(chromosomeType.getName()).append(": ").append(chromosome).append("\n");
		}

		return stringBuilder.toString();
	}

	private ImmutableMap<C, IAllele> getDefaultTemplate() {
		return getSpeciesRoot().getDefaultTemplate();
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
		Chromosome chromosome = Chromosome.create(chromosomeNBT);
		if (chromosome == null) {
			return null;
		}

		IAllele activeAllele = chromosome.getActiveAllele();
		if (!(activeAllele instanceof IAlleleSpecies)) {
			return null;
		}

		return (IAlleleSpecies) activeAllele;
	}

	private static <C extends Enum<C> & IChromosomeType<C>> IChromosome getChromosome(@Nonnull ItemStack itemStack, @Nonnull C chromosomeType, @Nonnull ISpeciesRoot<C> speciesRoot) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			return null;
		}

		NBTTagCompound genome = nbtTagCompound.getCompoundTag("Genome");
		if (genome == null) {
			return null;
		}

		ImmutableMap<C, IChromosome> chromosomes = getChromosomes(genome, speciesRoot);
		if (chromosomes == null) {
			return null;
		}

		return chromosomes.get(chromosomeType);
	}

	private static <C extends Enum<C> & IChromosomeType<C>> ImmutableMap<C, IChromosome> getChromosomes(@Nonnull NBTTagCompound genomeNBT, @Nonnull ISpeciesRoot<C> speciesRoot) {
		NBTTagList chromosomesNBT = genomeNBT.getTagList("Chromosomes", 10);
		ImmutableMap.Builder<C, IChromosome> chromosomesBuilder = ImmutableMap.builder();

		for (int i = 0; i < chromosomesNBT.tagCount(); i++) {
			NBTTagCompound chromosomeNBT = chromosomesNBT.getCompoundTagAt(i);
			byte chromosomeUid = chromosomeNBT.getByte(UID_KEY);

			Chromosome chromosome = Chromosome.create(chromosomeNBT);
			C chromosomeType = speciesRoot.getChromosomeTypeForUid(chromosomeUid);

			Class<? extends IAllele> alleleClass = chromosomeType.getAlleleClass();
			if (chromosome == null || chromosome.hasInvalidAlleles(alleleClass)) {
				if (Config.clearInvalidChromosomes) {
					Log.warning("Found Chromosome with invalid Alleles. Config is set to clear invalid chromosomes, replacing with the default.");
					ImmutableMap<C, IAllele> defaultTemplate = speciesRoot.getDefaultTemplate();
					return speciesRoot.templateAsChromosomes(defaultTemplate);
				} else {
					throw new RuntimeException("Found Chromosome with invalid Alleles.\nNBTTagCompound: " + chromosomesNBT + "\nSee config option \"genetics.clear.invalid.chromosomes\".\nMissing: " + chromosomeNBT);
				}
			}

			chromosomesBuilder.put(chromosomeType, chromosome);
		}

		return chromosomesBuilder.build();
	}

	protected static <C extends Enum<C> & IChromosomeType<C>> IAllele getActiveAllele(ItemStack itemStack, C chromosomeType, ISpeciesRoot<C> speciesRoot) {
		IChromosome chromosome = getChromosome(itemStack, chromosomeType, speciesRoot);
		if (chromosome == null) {
			return null;
		}
		return chromosome.getActiveAllele();
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagList nbttaglist = new NBTTagList();
		for (Map.Entry<C, IChromosome> entry : chromosomes.entrySet()) {
			C chromosomeType = entry.getKey();

			IChromosome chromosome = entry.getValue();
			byte chromosomeUid = chromosomeType.getUid();

			NBTTagCompound chromosomeNbt = new NBTTagCompound();
			chromosomeNbt.setByte(UID_KEY, chromosomeUid);
			chromosome.writeToNBT(chromosomeNbt);

			nbttaglist.appendTag(chromosomeNbt);
		}
		nbt.setTag("Chromosomes", nbttaglist);
	}

	// / INFORMATION RETRIEVAL
	@Nonnull
	@Override
	public ImmutableMap<C, IChromosome> getChromosomes() {
		return chromosomes;
	}

	@Nonnull
	@Override
	public IAllele getActiveAllele(C chromosomeType) {
		return chromosomes.get(chromosomeType).getActiveAllele();
	}

	@Nonnull
	@Override
	public IAllele getInactiveAllele(C chromosomeType) {
		return chromosomes.get(chromosomeType).getInactiveAllele();
	}

	@Override
	public boolean isGeneticEqual(IGenome<C> other) {
		ImmutableMap<C, IChromosome> genetics = other.getChromosomes();
		if (chromosomes.size() != genetics.size()) {
			return false;
		}

		for (Map.Entry<C, IChromosome> entry : chromosomes.entrySet()) {
			C chromosomeType = entry.getKey();
			IChromosome chromosome = entry.getValue();
			IChromosome otherChromosome = genetics.get(chromosomeType);

			if (!Chromosome.equals(chromosome, otherChromosome)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		Objects.ToStringHelper toStringHelper = Objects.toStringHelper(this);
		for (Map.Entry<C, IChromosome> entry : chromosomes.entrySet()) {
			C chromosomeType = entry.getKey();
			IChromosome chromosome = entry.getValue();
			toStringHelper.add(chromosomeType.getName(), chromosome);
		}
		return toStringHelper.toString();
	}
}
