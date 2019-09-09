/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * Holds a static reference to the {@link IGeneticRegistry}.
 */
public class AlleleManager {
	/**
	 * Main access point for all things related to genetics. See {@link IGeneticRegistry} for details.
	 */
	public static IGeneticRegistry geneticRegistry;

	/**
	 * Queryable instance of an {@link IClimateHelper} for easier implementation.
	 */
	public static IClimateHelper climateHelper;

	/**
	 * Creates Forestry alleles.
	 */
	public static IAlleleFactory alleleFactory;

	/**
	 * @since 5.8
	 */
	public static IFilterRegistry filterRegistry;

	public static IBreedingTrackerManager breedingTrackerManager;

}
