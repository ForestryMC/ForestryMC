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

import net.minecraftforge.registries.ObjectHolder;

public interface IMoistenerRecipe extends IForestryRecipe {

	RecipeType<IMoistenerRecipe> TYPE = RecipeManagers.create("forestry:moistener");

	class Companion {
		@ObjectHolder(registryName = "recipe_serializer", value = "forestry:moistener")
		public static final RecipeSerializer<IMoistenerRecipe> SERIALIZER = null;
	}

	/**
	 * Moistener runs at 1 - 4 time ticks per ingame tick depending on light level. For mycelium this value is currently 5000.
	 *
	 * @return moistener ticks to process one item.
	 */
	int getTimePerItem();

	/**
	 * @return Item required in resource stack. Will be reduced by one per produced item.
	 */
	Ingredient getResource();

	/**
	 * @return Item to produce per resource processed.
	 */
	ItemStack getProduct();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}
}
