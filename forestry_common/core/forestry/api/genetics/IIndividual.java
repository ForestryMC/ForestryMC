/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.List;

import forestry.api.core.INBTTagable;

/**
 * An actual individual with genetic information.
 * 
 * Only the default implementation is supported.
 */
public interface IIndividual extends INBTTagable {

	String getIdent();

	String getDisplayName();

	void addTooltip(List<String> list);

	/**
	 * Call to mark the IIndividual as analyzed. 
	 * @return true if the IIndividual has not been analyzed previously.
	 */
	boolean analyze();

	boolean isAnalyzed();

	boolean hasEffect();

	boolean isSecret();

	IGenome getGenome();

	/**
	 * Check whether the genetic makeup of two IIndividuals is identical. Ignores additional data like generations, irregular mating, etc..
	 * @param other
	 * @return true if the given other IIndividual has the amount of chromosomes and their alleles are identical.
	 */
	boolean isGeneticEqual(IIndividual other);

	/**
	 * @return A deep copy of this individual.
	 */
	IIndividual copy();
	
	/**
	 * @param chromosomeOrdinal Ordinal of the chromosome to check.
	 * @return true if both primary and secondary allele on the given chromosome match.
	 */
	boolean isPureBred(int chromosomeOrdinal);

}
