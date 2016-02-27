/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nonnull;
import java.util.List;

import forestry.api.core.INbtWritable;

/**
 * An actual individual with genetic information.
 *
 * Only the default implementation is supported.
 */
public interface IIndividual<C extends IChromosomeType> extends INbtWritable {

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

	@Nonnull
	IGenome<C> getGenome();

	/**
	 * Check whether the genetic makeup of two IIndividuals is identical. Ignores additional data like generations, irregular mating, etc..
	 * @param other
	 * @return true if the given other IIndividual has the amount of chromosomes and their alleles are identical.
	 */
	boolean isGeneticEqual(IIndividual<C> other);

	/**
	 * @return A deep copy of this individual.
	 */
	IIndividual<C> copy();

	boolean isPureBred(C chromosomeType);

}
