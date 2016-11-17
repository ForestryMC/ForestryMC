/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nonnull;

import forestry.api.core.INbtWritable;

/**
 * Holds the {@link IChromosome}s which comprise the traits of a given individual.
 *
 * Only the default implementation is supported.
 */
public interface IGenome extends INbtWritable {

	@Nonnull
	IAlleleSpecies getPrimary();

	@Nonnull
	IAlleleSpecies getSecondary();

	@Nonnull
	IChromosome[] getChromosomes();

	@Nonnull
	IAllele getActiveAllele(IChromosomeType chromosomeType);

	@Nonnull
	IAllele getInactiveAllele(IChromosomeType chromosomeType);

	boolean isGeneticEqual(IGenome other);

	@Nonnull
	ISpeciesRoot getSpeciesRoot();
}
