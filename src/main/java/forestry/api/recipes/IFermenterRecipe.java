/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface IFermenterRecipe extends IForestryRecipe, Comparable<IFermenterRecipe> {

	RecipeType<IFermenterRecipe> TYPE = RecipeManagers.create("forestry:fermenter");

	class Companion {
		@ObjectHolder(registryName = "recipe_serializer", value = "forestry:fermenter")
		public static final RecipeSerializer<IFermenterRecipe> SERIALIZER = null;
	}

	/**
	 * @return ItemStack representing the input resource.
	 */
	Ingredient getResource();

	/**
	 * @return FluidStack representing the input fluid resource.
	 */
	FluidStack getFluidResource();

	/**
	 * @return Value of the given resource, i.e. how much needs to be fermented for the output to be deposited into the product tank.
	 */
	int getFermentationValue();

	/**
	 * @return Modifies the amount of liquid output per work cycle.
	 * (water = 1.0f, honey = 1.5f)
	 */
	float getModifier();

	/**
	 * @return Fluid representing output. Amount is determined by fermentationValue * modifier.
	 */
	Fluid getOutput();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
