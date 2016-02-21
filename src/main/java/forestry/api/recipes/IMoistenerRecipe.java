/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;

public interface IMoistenerRecipe extends IForestryRecipe {
	/**
	 * Moistener runs at 1 - 4 time ticks per ingame tick depending on light level. For mycelium this value is currently 5000.
	 * @return moistener ticks to process one item.
	 */
	int getTimePerItem();

	/**
	 * @return Item required in resource stack. Will be reduced by one per produced item.
	 */
	ItemStack getResource();

	/**
	 * @return Item to produce per resource processed.
	 */
	ItemStack getProduct();
}
