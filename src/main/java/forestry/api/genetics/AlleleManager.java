/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Holds a static reference to the {@link IAlleleRegistry}. 
 */
public class AlleleManager {
	/**
	 * Main access point for all things related to genetics. See {@link IAlleleRegistry} for details.
	 */
	public static IAlleleRegistry alleleRegistry;

	/**
	 * Translates plain leaf blocks into genetic data. Used by bees and butterflies to convert and pollinate foreign leaf blocks.
	 */
	public static Map<Block, ILeafTranslator> leafTranslators = new HashMap<>();

	/**
	 * Translates plain saplings into genetic data. Used by the treealyzer and the farm to convert foreign saplings.
	 */
	public static Map<Item, ISaplingTranslator> saplingTranslation = new HashMap<>();

	/**
	 * Queryable instance of an {@link IClimateHelper} for easier implementation.
	 */
	public static IClimateHelper climateHelper;

	/**
	 * Creates Forestry alleles.
	 */
	public static IAlleleFactory alleleFactory;
}
