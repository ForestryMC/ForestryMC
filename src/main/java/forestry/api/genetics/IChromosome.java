/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Random;

import forestry.api.core.INbtWritable;

/**
 * Contains two alleles. One is active and the other is inactive.
 *
 * The active allele is the active allele either because the allele is dominant or
 * because both alleles are recessive.
 * <p>
 * Implementations other than Forestry's default one are not supported!
 *
 * You can uses {@link IGeneticFactory#createChromosome(IAllele, IAllele)} to create an instance of this.
 *
 * @author SirSengir
 */
public interface IChromosome extends INbtWritable {

	/**
	 * @return The same allele like {@link #getActiveAllele()}.
	 *
	 * @deprecated We do not need two methods that return the same value.
	 */
	@Deprecated
	IAllele getPrimaryAllele();

	/**
	 * @return The same allele like {@link #getInactiveAllele()}.
	 *
	 * @deprecated We do not need two methods that return the same value.
	 */
	@Deprecated
	IAllele getSecondaryAllele();

	/**
	 * @return The active allele of this chromosome that is used in the most situations.
	 */
	IAllele getActiveAllele();

	/**
	 * @return The inactive allele of this chromosome.
	 */
	IAllele getInactiveAllele();

	/**
	 * Crates a new chromosome out of the alleles of this chromosome and the other chromosome.
	 *
	 * It always uses one allele from this and one from the other chromosome to create the new chromosome.
	 * It uses {@link Random#nextBoolean()} to decide which of the two alleles of one chromosome it should use.
	 *
	 * @param rand 				 The instance of random it should uses to figure out which of the two alleles if should
	 *                           use.
	 * @param otherChromosome    The other chromosome that this chromosome uses to create the new one.
	 */
	IChromosome inheritChromosome(Random rand, IChromosome otherChromosome);

}
