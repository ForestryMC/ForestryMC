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
import com.google.gson.JsonObject;
import forestry.api.recipes.IMoistenerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MoistenerRecipe implements IMoistenerRecipe {
    private final ResourceLocation id;
    private final int timePerItem;
    private final Ingredient resource;
    private final ItemStack product;

    public MoistenerRecipe(ResourceLocation id, Ingredient resource, ItemStack product, int timePerItem) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(resource, "Resource cannot be null");
        Preconditions.checkNotNull(product, "Product cannot be null");

        this.id = id;
        this.timePerItem = timePerItem;
        this.resource = resource;
        this.product = product;
    }

    @Override
    public int getTimePerItem() {
        return timePerItem;
    }

    @Override
    public Ingredient getResource() {
        return resource;
    }

    @Override
    public ItemStack getProduct() {
        return product;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MoistenerRecipe> {
        @Override
        public MoistenerRecipe read(ResourceLocation recipeId, JsonObject json) {
            int timePerItem = JSONUtils.getInt(json, "time");
            Ingredient resource = Ingredient.deserialize(json.get("resource"));
            ItemStack product = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "product"));

            return new MoistenerRecipe(recipeId, resource, product, timePerItem);
        }

        @Override
        public MoistenerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int timePerItem = buffer.readVarInt();
            Ingredient resource = Ingredient.read(buffer);
            ItemStack product = buffer.readItemStack();

            return new MoistenerRecipe(recipeId, resource, product, timePerItem);
        }

        @Override
        public void write(PacketBuffer buffer, MoistenerRecipe recipe) {
            buffer.writeVarInt(recipe.timePerItem);
            recipe.resource.write(buffer);
            buffer.writeItemStack(recipe.product);
        }
    }
}
