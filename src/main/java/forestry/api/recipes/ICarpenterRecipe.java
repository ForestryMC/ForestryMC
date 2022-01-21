/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface ICarpenterRecipe extends IForestryRecipe {

	RecipeType<ICarpenterRecipe> TYPE = RecipeManagers.create("forestry:carpenter");

	class Companion {
		@ObjectHolder("forestry:carpenter")
		public static final RecipeSerializer<ICarpenterRecipe> SERIALIZER = null;
	}

	/**
	 * @return Number of work cycles required to craft the recipe once.
	 */
	int getPackagingTime();

	/**
	 * @return the crafting grid recipe. The crafting recipe's getRecipeOutput() is used as the ICarpenterRecipe's output.
	 */
	CraftingRecipe getCraftingGridRecipe();

	/**
	 * @return The crafting result of this recipe
	 */
	ItemStack getResult();

	/**
	 * @return the box required for this recipe. return empty stack if there is no required box.
	 * Examples of boxes are the Forestry cartons and crates.
	 */
	Ingredient getBox();

	/**
	 * @return the fluid required for this recipe. return null if there is no required fluid.
	 */
	@Nullable
	FluidStack getFluidResource();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
