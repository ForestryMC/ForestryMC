/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

public interface ICentrifugeRecipe extends IForestryRecipe {
    IRecipeType<ICentrifugeRecipe> TYPE = RecipeManagers.create("forestry:centrifuge");

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
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }

    class Companion {
        @ObjectHolder("forestry:centrifuge")
        public static final IRecipeSerializer<ICentrifugeRecipe> SERIALIZER = null;
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
