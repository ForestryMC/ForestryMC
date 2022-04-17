/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorRecipe extends IForestryRecipe {

	/**
	 * @return the molten liquid (and amount) required for this recipe.
	 */
	FluidStack getLiquid();

	/**
	 * @return the list of ingredients in the crafting grid to create this recipe.
	 */
	Object[] getIngredients();

	/**
	 * @return the width of ingredients in the crafting grid to create this recipe.
	 */
	int getWidth();

	/**
	 * @return the height of ingredients in the crafting grid to create this recipe.
	 */
	int getHeight();

	/**
	 * @return true if this recipe copies the NBT from input items to output items
	 * @deprecated since Forestry 4.2 wood itemStacks use item damage (meta) instead of NBT
	 */
	@Deprecated
	boolean preservesNbt();

	/**
	 * @return the plan for this recipe (the item in the top right slot).
	 */
	@Nullable
	ItemStack getPlan();

	/**
	 * @return the result of this recipe
	 */
	ItemStack getRecipeOutput();

	/**
	 * Returns an Item that is the result of this recipe
	 * @deprecated since Forestry 4.1. Forestry uses getRecipeOutput() and preservesNbt() to determine the result.
	 */
	@Deprecated
	ItemStack getCraftingResult(IInventory craftingInventory);

	/**
	 * @param plan The Fabricator plan, the item in the top right slot.
	 * @param resources The resources in the crafting grid.
	 * @return true if the plan and resources match this recipe.
	 * @deprecated since Forestry 4.1. This is handled by Forestry instead.
	 */
	@Deprecated
	boolean matches(@Nullable ItemStack plan, ItemStack[][] resources);
}
