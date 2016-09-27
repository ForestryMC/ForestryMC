/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * Extension of IRecipe to give better access to the expected crafting inputs.
 * It can be matched against regular IInventory instead of just InventoryCrafting.
 * Useful for displaying crafting recipes.
 */
public interface IDescriptiveRecipe extends IRecipe {

	/**
	 * @return width of the crafting ingredients in the crafting table
	 */
	int getWidth();

	/**
	 * @return height of the crafting ingredients in the crafting table
	 */
	int getHeight();

	/**
	 * @return array of all the ingredients in the crafting table.
	 * Ingredients may be null, ItemStack, ItemStack[], or a List of ItemStack
	 */
	Object[] getIngredients();

	@Nonnull
	@Override
	ItemStack getRecipeOutput();
}
