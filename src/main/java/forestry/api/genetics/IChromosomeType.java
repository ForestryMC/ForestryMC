/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

/*
 * Interface to be implemented by the enums representing the various chromosomes
 */
public interface IChromosomeType {

	/*
	 * Get class which all alleles on this chromosome must interface
	 */
	Class<? extends IAllele> getAlleleClass();

	/**
	 * @return Short identifier.
	 */
	String getName();

	ISpeciesRoot getSpeciesRoot();

	/**
	 * @return The position of this chromosome type at a chromosome array.
	 */
	int ordinal();

}
