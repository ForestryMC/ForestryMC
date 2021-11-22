/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the suqeezer.
 * 
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * 
 * Accessible via {@link RecipeManagers}
 * 
 * @author SirSengir
 */
public interface ISqueezerManager extends ICraftingProvider<ISqueezerRecipe> {

	/**
	 * Add a recipe to the squeezer.
	 * 
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param resources Array of item stacks representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid {@link FluidStack} representing the output of this recipe.
	 * @param remnants Item stack representing the possible remnants from this recipe.
	 * @param chance Chance remnants will be produced by a single recipe cycle, from 0 to 100.
	 */
	public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid, ItemStack remnants, int chance);

	/**
	 * Add a recipe to the squeezer.
	 * 
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param resources Array of item stacks representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid {@link FluidStack} representing the output of this recipe.
	 */
	public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid);

	/**
	 * Add a recipe for a fluid container to the squeezer.
	 * This will add recipes to get all types of liquids out of this type of fluid container.
	 *
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param emptyContainer The empty version of the fluid container that will be squeezed.
	 * @param remnants Item stack representing the possible remnants from this recipe.
	 * @param chance Chance remnants will be produced by a single recipe cycle, from 0 to 1.
	 */
	void addContainerRecipe(int timePerItem, ItemStack emptyContainer, @Nullable ItemStack remnants, float chance);
}
