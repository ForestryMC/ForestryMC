/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import java.util.Random;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

import net.minecraftforge.registries.ObjectHolder;

public interface ICentrifugeRecipe extends IForestryRecipe {

	RecipeType<ICentrifugeRecipe> TYPE = RecipeManagers.create("forestry:centrifuge");

	class Companion {
		@ObjectHolder("forestry:centrifuge")
		public static final RecipeSerializer<ICentrifugeRecipe> SERIALIZER = null;
	}

	/**
	 * The item for this recipe to match against.
	 **/
	Ingredient getInput();

	/**
	 * The time it takes to process one item. Default is 20.
	 **/
	int getProcessingTime();

	/**
	 * Returns the randomized products from processing one input item.
	 **/
	NonNullList<ItemStack> getProducts(Random random);

	/**
	 * Returns a list of all possible products and their estimated probabilities (0.0 to 1.0],
	 * to help mods that display recipes
	 **/
	NonNullList<Product> getAllProducts();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return Companion.SERIALIZER;
	}

	class Product {
		private final float probability;
		private final ItemStack stack;

		public Product(float probability, ItemStack stack) {
			this.probability = probability;
			this.stack = stack;
		}

		public float getProbability() {
			return probability;
		}

		public ItemStack getStack() {
			return stack;
		}
	}
}
