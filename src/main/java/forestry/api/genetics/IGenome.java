/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

import forestry.api.core.INbtWritable;

/**
 * Holds the {@link IChromosome}s which comprise the traits of a given individual.
 *
 * Only the default implementation is supported.
 */
public interface IGenome<C extends IChromosomeType> extends INbtWritable {

	@Nonnull
	IAlleleSpecies<C> getPrimary();

	@Nonnull
	IAlleleSpecies<C> getSecondary();

	@Nonnull
	ImmutableMap<C, IChromosome> getChromosomes();

	@Nonnull
	IAllele getActiveAllele(C chromosomeType);

	@Nonnull
	IAllele getInactiveAllele(C chromosomeType);

	boolean isGeneticEqual(IGenome<C> other);

	@Nonnull
	ISpeciesRoot<C> getSpeciesRoot();
}
