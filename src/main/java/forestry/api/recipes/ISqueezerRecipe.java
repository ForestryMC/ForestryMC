/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface ISqueezerRecipe extends IForestryRecipe {

	RecipeType<ISqueezerRecipe> TYPE = RecipeManagers.create("forestry:squeezer");

	class Companion {
		@ObjectHolder(registryName = "recipe_serializer", value = "forestry:squeezer")
		public static final RecipeSerializer<ISqueezerRecipe> SERIALIZER = null;
	}

	/**
	 * @return item stacks representing the required resources for one process. Stack size will be taken into account.
	 */
	NonNullList<Ingredient> getResources();

	/**
	 * @return Number of work cycles required to squeeze one set of resources.
	 */
	int getProcessingTime();

	/**
	 * @return Item stack representing the possible remnants from this recipe. (i.e. tin left over from tin cans)
	 */
	ItemStack getRemnants();

	/**
	 * @return Chance remnants will be produced by a single recipe cycle, from 0 to 1.
	 */
	float getRemnantsChance();

	/**
	 * @return {@link FluidStack} representing the output of this recipe.
	 */
	FluidStack getFluidOutput();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
