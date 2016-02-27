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

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Config;

public abstract class Individual<C extends IChromosomeType> implements IIndividual<C> {

	protected boolean isAnalyzed;

	protected Individual() {
		isAnalyzed = false;
	}

	protected Individual(@Nonnull NBTTagCompound nbt) {
		isAnalyzed = nbt.getBoolean("IsAnalyzed");
	}

	@Override
	public boolean isAnalyzed() {
		return isAnalyzed;
	}

	@Override
	public boolean analyze() {
		if (isAnalyzed) {
			return false;
		}

		isAnalyzed = true;
		return true;
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("IsAnalyzed", isAnalyzed);
	}

	/* IDENTIFICATION */
	@Override
	public String getIdent() {
		return getGenome().getPrimary().getUID();
	}

	@Override
	public String getDisplayName() {
		return getGenome().getPrimary().getName();
	}

	/* INFORMATION */
	@Override
	public boolean hasEffect() {
		return getGenome().getPrimary().hasEffect();
	}

	@Override
	public boolean isSecret() {
		return getGenome().getPrimary().isSecret();
	}

	@Override
	public boolean isGeneticEqual(IIndividual<C> other) {
		return getGenome().isGeneticEqual(other.getGenome());
	}

	@Override
	public boolean isPureBred(C chromosomeType) {
		return getGenome().getActiveAllele(chromosomeType).getUID().equals(getGenome().getInactiveAllele(chromosomeType).getUID());
	}

	private ImmutableMap<C, IChromosome> mutateSpecies(World world, @Nullable GameProfile playerProfile, BlockPos pos, IGenome<C> genomeOne, IGenome<C> genomeTwo) {
		IGenome<C> genome0;
		IGenome<C> genome1;
		IAlleleSpecies<C> allele0;
		IAlleleSpecies<C> allele1;

		ISpeciesRoot<C> root = genomeOne.getSpeciesRoot();

		if (world.rand.nextBoolean()) {
			allele0 = genomeOne.getPrimary();
			allele1 = genomeTwo.getSecondary();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = genomeTwo.getPrimary();
			allele1 = genomeOne.getSecondary();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		final IBreedingTracker<C> breedingTracker;
		if (playerProfile != null) {
			breedingTracker = root.getBreedingTracker(world, playerProfile);
		} else {
			breedingTracker = null;
		}

		List<IMutation<C>> combinations = root.getCombinations(allele0, allele1, true);
		for (IMutation<C> mutation : combinations) {
			float chance = mutation.getChance(world, pos, allele0, allele1, genome0, genome1);
			if (chance <= 0) {
				continue;
			}

			// boost chance for researched mutations
			if (breedingTracker != null && breedingTracker.isResearched(mutation)) {
				float mutationBoost = chance * (Config.researchMutationBoostMultiplier - 1.0f);
				mutationBoost = Math.min(Config.maxResearchMutationBoostPercent, mutationBoost);
				chance += mutationBoost;
			}

			if (chance > world.rand.nextFloat() * 100) {
				return root.templateAsChromosomes(mutation.getResultTemplate());
			}
		}

		return null;
	}

	@Nonnull
	protected ImmutableMap<C, IChromosome> createOffspringChromosomes(@Nonnull World world, @Nullable GameProfile playerProfile, @Nonnull BlockPos pos, @Nonnull IGenome<C> genome, @Nonnull IGenome<C> mate) {
		ImmutableMap.Builder<C, IChromosome> chromosomeBuilder = ImmutableMap.builder();
		ImmutableMap<C, IChromosome> parent0 = genome.getChromosomes();
		ImmutableMap<C, IChromosome> parent1 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation template if mutation occurred.
		ImmutableMap<C, IChromosome> mutated0 = mutateSpecies(world, playerProfile, pos, genome, mate);
		if (mutated0 != null) {
			parent0 = mutated0;
		}
		ImmutableMap<C, IChromosome> mutated1 = mutateSpecies(world, playerProfile, pos, mate, genome);
		if (mutated1 != null) {
			parent1 = mutated1;
		}

		for (Map.Entry<C, IChromosome> entry : parent0.entrySet()) {
			C chromosomeType = entry.getKey();
			IChromosome parentChromosome0 = entry.getValue();
			IChromosome parentChromosome1 = parent1.get(chromosomeType);
			IChromosome chromosome = Chromosome.inheritChromosome(world.rand, parentChromosome0, parentChromosome1);
			chromosomeBuilder.put(chromosomeType, chromosome);
		}

		return chromosomeBuilder.build();
	}
}
