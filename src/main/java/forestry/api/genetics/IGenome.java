/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.INBTTagable;

/**
 * Holds the {@link IChromosome}s which comprise the traits of a given individual.
 * 
 * Only the default implementation is supported.
 */
public interface IGenome extends INBTTagable {

	IAlleleSpecies getPrimary();

	IAlleleSpecies getSecondary();

	IChromosome[] getChromosomes();

	/**
	 * @deprecated since Forestry 3.3. Use IChromosomeType version
	 */
	@Deprecated
	IAllele getActiveAllele(int chromosome);
	IAllele getActiveAllele(IChromosomeType chromosomeType);

	/**
	 * @deprecated since Forestry 3.3. Use IChromosomeType version
	 */
	@Deprecated
	IAllele getInactiveAllele(int chromosome);
	IAllele getInactiveAllele(IChromosomeType chromosomeType);

	boolean isGeneticEqual(IGenome other);
	
	ISpeciesRoot getSpeciesRoot();
}
