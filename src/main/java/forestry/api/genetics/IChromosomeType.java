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

	String getName();

	ISpeciesRoot getSpeciesRoot();

	int ordinal();

	/**
	 * @return A short identifier used to save the alleles of the chromosome in the NBT data.
	 * @since Forestry 5.8
	 */
	default String getShortName(){
		return getName();
	}

	/**
	 * @return True if the alleles of this chromosome are needed on the client side.
	 * 	 * @since Forestry 5.8
	 */
	default boolean isNeededOnClientSide(){
		return false;
	}

}
