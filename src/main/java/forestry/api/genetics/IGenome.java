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

	/**
	 * @return The active species of this genome.
	 */
	IAlleleSpecies getPrimary();

	/**
	 * @return The inactive species of this genome.
	 */
	IAlleleSpecies getSecondary();

	/**
	 * @return A array with all chromosomes of this genome.
	 */
	IChromosome[] getChromosomes();

	/**
	 * @return The active allele of this chromosome type.
	 */
	IAllele getActiveAllele(IChromosomeType chromosomeType);

	/**
	 * @return The inactive allele of this chromosome type.
	 */
	IAllele getInactiveAllele(IChromosomeType chromosomeType);

	/**
	 * @return true if the given other IGenome has the amount of chromosomes and their alleles are identical.
	 */
	boolean isGeneticEqual(IGenome other);

	/**
	 * @return The root of this species and describes the class of species of this genome.
	 */
	ISpeciesRoot getSpeciesRoot();
}
