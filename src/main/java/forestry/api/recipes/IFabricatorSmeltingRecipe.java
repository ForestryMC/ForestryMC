/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface IFabricatorSmeltingRecipe extends IForestryRecipe {

	RecipeType<IFabricatorSmeltingRecipe> TYPE = RecipeManagers.create("forestry:fabricator_smelting");

	class Companion {
		@ObjectHolder("forestry:fabricator_smelting")
		public static final RecipeSerializer<IFabricatorSmeltingRecipe> SERIALIZER = null;
	}

	/**
	 * @return item to be melted down
	 */
	Ingredient getResource();

	/**
	 * @return temperature at which the item melts. Glass is 1000, Sand is 3000.
	 */
	int getMeltingPoint();

	/**
	 * @return resulting fluid
	 */
	FluidStack getProduct();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
