/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import forestry.api.recipes.ICentrifugeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Random;

public class CentrifugeRecipe implements ICentrifugeRecipe {
    private final ResourceLocation id;
    private final int processingTime;
    private final Ingredient input;
    private final NonNullList<Product> outputs;

    public CentrifugeRecipe(ResourceLocation id, int processingTime, Ingredient input, NonNullList<Product> outputs) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");

        this.id = id;
        this.processingTime = processingTime;
        this.input = input;
        this.outputs = outputs;
    }

    @Override
    public Ingredient getInput() {
        return input;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public NonNullList<ItemStack> getProducts(Random random) {
        NonNullList<ItemStack> products = NonNullList.create();

        for (Product entry : this.outputs) {
            float probability = entry.getProbability();

            if (probability >= 1.0) {
                products.add(entry.getStack().copy());
            } else if (random.nextFloat() < probability) {
                products.add(entry.getStack().copy());
            }
        }

        return products;
    }

    @Override
    public NonNullList<Product> getAllProducts() {
        return outputs;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugeRecipe> {
        @Override
        public CentrifugeRecipe read(ResourceLocation recipeId, JsonObject json) {
            int processingTime = JSONUtils.getInt(json, "time");
            Ingredient input = Ingredient.deserialize(json.get("input"));
            NonNullList<Product> outputs = NonNullList.create();

            for (JsonElement element : JSONUtils.getJsonArray(json, "products")) {
                float chance = JSONUtils.getInt(element.getAsJsonObject(), "chance");
                ItemStack stack = CraftingHelper.getItemStack(JSONUtils.getJsonObject(
                        element.getAsJsonObject(),
                        "item"
                ), true);
                outputs.add(new Product(chance, stack));
            }

            return new CentrifugeRecipe(recipeId, processingTime, input, outputs);
        }

        @Override
        public CentrifugeRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int processingTime = buffer.readVarInt();
            Ingredient input = Ingredient.read(buffer);
            NonNullList<Product> outputs = RecipeSerializers.read(buffer, b -> {
                float chance = b.readFloat();
                ItemStack stack = b.readItemStack();
                return new Product(chance, stack);
            });

            return new CentrifugeRecipe(recipeId, processingTime, input, outputs);
        }

        @Override
        public void write(PacketBuffer buffer, CentrifugeRecipe recipe) {
            buffer.writeVarInt(recipe.processingTime);
            recipe.input.write(buffer);

            RecipeSerializers.write(buffer, recipe.outputs, (b, product) -> {
                b.writeFloat(product.getProbability());
                b.writeItemStack(product.getStack());
            });
        }
    }
}
