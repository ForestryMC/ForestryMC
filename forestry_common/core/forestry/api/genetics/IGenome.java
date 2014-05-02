/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

	IAllele getActiveAllele(int chromosome);

	IAllele getInactiveAllele(int chromosome);

	boolean isGeneticEqual(IGenome other);
	
	ISpeciesRoot getSpeciesRoot();
}
