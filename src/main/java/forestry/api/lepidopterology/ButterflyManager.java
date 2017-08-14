/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

public class ButterflyManager {

	/**
	 * Convenient access to AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies")
	 */
	public static IButterflyRoot butterflyRoot;

	/**
	 * Used to create new butterflies.
	 */
	public static IButterflyFactory butterflyFactory;

	/**
	 * Used to create new butterfly mutations.
	 */
	public static IButterflyMutationFactory butterflyMutationFactory;
}
