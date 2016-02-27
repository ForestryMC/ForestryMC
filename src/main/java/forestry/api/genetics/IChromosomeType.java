/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nonnull;

/*
 * Interface to be implemented by the enums representing the various chromosomes
 */
public interface IChromosomeType<C extends IChromosomeType<C>> {

	/*
	 * Get class which all alleles on this chromosome must interface
	 */
	@Nonnull
	Class<? extends IAllele> getAlleleClass();

	@Nonnull
	String getName();

	@Nonnull
	ISpeciesRoot<C> getSpeciesRoot();

	byte getUid();

}
