/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface IStillRecipe extends IForestryRecipe {

	RecipeType<IStillRecipe> TYPE = RecipeManagers.create("forestry:still");

	class Companion {
		@ObjectHolder("forestry:still")
		public static final RecipeSerializer<IStillRecipe> SERIALIZER = null;
	}

	/**
	 * @return Amount of work cycles required to run through the conversion once.
	 */
	int getCyclesPerUnit();

	/**
	 * @return FluidStack representing the input liquid.
	 */
	FluidStack getInput();

	/**
	 * @return FluidStack representing the output liquid.
	 */
	FluidStack getOutput();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
