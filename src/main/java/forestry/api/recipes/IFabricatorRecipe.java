/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorRecipe extends IForestryRecipe {

	/**
	 * @return the molten liquid (and amount) required for this recipe.
	 */
	FluidStack getLiquid();

	/**
	 * @return the list of ingredients in the crafting grid to create this recipe.
	 * Each inner list represents one slot's accepted ItemStacks
	 */
	NonNullList<NonNullList<ItemStack>> getIngredients();
	
	NonNullList<String> getOreDicts();

	/**
	 * @return the width of ingredients in the crafting grid to create this recipe.
	 */
	int getWidth();

	/**
	 * @return the height of ingredients in the crafting grid to create this recipe.
	 */
	int getHeight();

	/**
	 * @return the plan for this recipe (the item in the top right slot). may be an empty ItemStack
	 */
	ItemStack getPlan();

	/**
	 * @return the result of this recipe
	 */
	ItemStack getRecipeOutput();
}
