/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.INbtWritable;

/**
 * Holds the {@link IChromosome}s which comprise the traits of a given individual.
 * <p>
 * Only the default implementation is supported.
 */
public interface IGenome extends INbtWritable {

	IAlleleSpecies getPrimary();

	IAlleleSpecies getSecondary();

	IChromosome[] getChromosomes();

	IAllele getActiveAllele(IChromosomeType chromosomeType);

	IAllele getInactiveAllele(IChromosomeType chromosomeType);

	boolean isGeneticEqual(IGenome other);

	ISpeciesRoot getSpeciesRoot();
}
