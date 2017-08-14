/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

/**
 * Replacement for IRecipe to give better access to the expected crafting inputs.
 * It can be matched against regular IInventory instead of just InventoryCrafting.
 * Useful for displaying crafting recipes.
 */
public interface IDescriptiveRecipe {

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
	 * Each inner list represents one slot's accepted ItemStacks
	 */
	NonNullList<NonNullList<ItemStack>> getRawIngredients();
	
	NonNullList<String> getOreDicts();

	/**
	 * Must not be named the same as {@link IRecipe#getRecipeOutput()} to avoid obfuscation for recipes that implement both.
	 */
	ItemStack getOutput();
}
