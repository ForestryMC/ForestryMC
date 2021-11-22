/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

public interface ICarpenterRecipe extends IForestryRecipe {

	/**
	 * @return Number of work cycles required to craft the recipe once.
	 */
	int getPackagingTime();

	/**
	 * @return the crafting grid recipe. The crafting recipe's getRecipeOutput() is used as the ICarpenterRecipe's output.
	 */
	IDescriptiveRecipe getCraftingGridRecipe();

	/**
	 * @return the box required for this recipe. return null if there is no required box.
	 * Examples of boxes are the Forestry cartons and crates.
	 */
	@Nullable
	ItemStack getBox();

	/**
	 * @return the fluid required for this recipe. return null if there is no required fluid.
	 */
	@Nullable
	FluidStack getFluidResource();

}
