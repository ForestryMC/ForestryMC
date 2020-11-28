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
import forestry.api.recipes.ICarpenterRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CarpenterRecipe implements ICarpenterRecipe {
    private final ResourceLocation id;
    private final int packagingTime;
    private final FluidStack liquid;
    private final Ingredient box;
    private final ShapedRecipe recipe;

    public CarpenterRecipe(
            ResourceLocation id,
            int packagingTime,
            FluidStack liquid,
            Ingredient box,
            ShapedRecipe recipe
    ) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(box);
        Preconditions.checkNotNull(recipe);

        this.id = id;
        this.packagingTime = packagingTime;
        this.liquid = liquid;
        this.box = box;
        this.recipe = recipe;
    }

    @Override
    public int getPackagingTime() {
        return packagingTime;
    }

    @Override
    public Ingredient getBox() {
        return box;
    }

    @Override
    public FluidStack getFluidResource() {
        return liquid;
    }

    @Override
    public ShapedRecipe getCraftingGridRecipe() {
        return recipe;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CarpenterRecipe> {
        @Override
        public CarpenterRecipe read(ResourceLocation recipeId, JsonObject json) {
            int packagingTime = JSONUtils.getInt(json, "time");

            JsonElement liquidElement = json.get("liquid");
            FluidStack liquid = FluidStack.EMPTY;
            if (liquidElement != null) {
                liquid = RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "liquid"));
            }

            JsonElement boxElement = json.get("box");
            Ingredient box = Ingredient.fromStacks(ItemStack.EMPTY);
            if (boxElement != null) {
                box = Ingredient.deserialize(json.get("box"));
            }

            ShapedRecipe internal = IRecipeSerializer.CRAFTING_SHAPED.read(
                    recipeId,
                    JSONUtils.getJsonObject(json, "recipe")
            );

            return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal);
        }

        @Override
        public CarpenterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int packagingTime = buffer.readVarInt();
            FluidStack liquid = FluidStack.readFromPacket(buffer);
            Ingredient box = Ingredient.read(buffer);
            ShapedRecipe internal = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer);

            return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal);
        }

        @Override
        public void write(PacketBuffer buffer, CarpenterRecipe recipe) {
            buffer.writeVarInt(recipe.packagingTime);
            recipe.liquid.writeToPacket(buffer);
            recipe.box.write(buffer);
            IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe.recipe);
        }
    }
}
