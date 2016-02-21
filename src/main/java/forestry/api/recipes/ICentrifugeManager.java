/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import java.util.Map;

import net.minecraft.item.ItemStack;

/**
 * Provides an interface to the recipe manager of the centrifuge.
 * 
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * 
 * Accessible via {@link RecipeManagers}
 * 
 * @author SirSengir
 */
public interface ICentrifugeManager extends ICraftingProvider<ICentrifugeRecipe> {

	/**
	 * Add a recipe to the centrifuge
	 *
	 * @param timePerItem Time to centrifugate one item of the given type. Default is 20.
	 * @param input ItemStack containing information on item id and damage. Stack size will be ignored.
	 * @param products Specifies the possible products and the chances of them resulting from centrifuging.
	 *                 Chances are from (0.0 to 1.0]
	 */
	void addRecipe(int timePerItem, ItemStack input, Map<ItemStack, Float> products);

}
