/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.core.NonNullList;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the suqeezer.
 * <p>
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * <p>
 * Accessible via {@link RecipeManagers}
 *
 * @author SirSengir
 */
public interface ISqueezerManager extends ICraftingProvider<ISqueezerRecipe> {

	/**
	 * Add a recipe to the squeezer.
	 *
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param resources   Array of item stacks representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid      {@link FluidStack} representing the output of this recipe.
	 * @param remnants    Item stack representing the possible remnants from this recipe. May be empty.
	 * @param chance      Chance remnants will be produced by a single recipe cycle, from 0 to 100.
	 */
	void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid, ItemStack remnants, int chance);

	/**
	 * Add a recipe to the squeezer.
	 *
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param resource   item stack representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid      {@link FluidStack} representing the output of this recipe.
	 * @param remnants    Item stack representing the possible remnants from this recipe. May be empty.
	 * @param chance      Chance remnants will be produced by a single recipe cycle, from 0 to 100.
	 */
	void addRecipe(int timePerItem, Ingredient resource, FluidStack liquid, ItemStack remnants, int chance);

	/**
	 * Add a recipe to the squeezer.
	 *
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param resources   Array of item stacks representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid      {@link FluidStack} representing the output of this recipe.
	 */
	void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid);

	/**
	 * Add a recipe to the squeezer.
	 *
	 * @param timePerItem Number of work cycles required to squeeze one set of resources.
	 * @param resource   item stack representing the required resources for one process. Stack size will be taken into account.
	 * @param liquid      {@link FluidStack} representing the output of this recipe.
	 */
	void addRecipe(int timePerItem, Ingredient resource, FluidStack liquid);

	@Nullable
	ISqueezerRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, NonNullList<ItemStack> items);

	boolean canUse(@Nullable RecipeManager recipeManager, ItemStack itemStack);
}
