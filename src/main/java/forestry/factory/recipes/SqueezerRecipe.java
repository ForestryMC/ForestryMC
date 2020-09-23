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
import forestry.api.recipes.ISqueezerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SqueezerRecipe implements ISqueezerRecipe {
    private final ResourceLocation id;
    private final int processingTime;
    private final NonNullList<ItemStack> resources;
    private final FluidStack fluidOutput;
    private final ItemStack remnants;
    private final float remnantsChance;

    public SqueezerRecipe(
            ResourceLocation id,
            int processingTime,
            NonNullList<ItemStack> resources,
            FluidStack fluidOutput,
            ItemStack remnants,
            float remnantsChance
    ) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(resources);
        Preconditions.checkArgument(!resources.isEmpty());
        Preconditions.checkNotNull(fluidOutput);
        Preconditions.checkNotNull(remnants);

        this.id = id;
        this.processingTime = processingTime;
        this.resources = resources;
        this.fluidOutput = fluidOutput;
        this.remnants = remnants;
        this.remnantsChance = remnantsChance;
    }

    @Override
    public NonNullList<ItemStack> getResources() {
        return resources;
    }

    @Override
    public ItemStack getRemnants() {
        return remnants;
    }

    @Override
    public float getRemnantsChance() {
        return remnantsChance;
    }

    @Override
    public FluidStack getFluidOutput() {
        return fluidOutput;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SqueezerRecipe> {
        @Override
        public SqueezerRecipe read(ResourceLocation recipeId, JsonObject json) {
            int processingTime = JSONUtils.getInt(json, "time");
            NonNullList<ItemStack> resources = NonNullList.create();
            FluidStack fluidOutput = RecipeSerializers.load(JSONUtils.getJsonObject(json, "output"));
            ItemStack remnants = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "remnant"));
            float remnantsChance = JSONUtils.getFloat(json, "chance");

            for (JsonElement element : JSONUtils.getJsonArray(json, "resources")) {
                resources.add(ShapedRecipe.deserializeItem(element.getAsJsonObject()));
            }

            return new SqueezerRecipe(recipeId, processingTime, resources, fluidOutput, remnants, remnantsChance);
        }

        @Override
        public SqueezerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int processingTime = buffer.readVarInt();
            NonNullList<ItemStack> resources = RecipeSerializers.read(buffer, PacketBuffer::readItemStack);
            FluidStack fluidOutput = FluidStack.readFromPacket(buffer);
            ItemStack remnants = buffer.readItemStack();
            float remnantsChance = buffer.readFloat();

            return new SqueezerRecipe(recipeId, processingTime, resources, fluidOutput, remnants, remnantsChance);
        }

        @Override
        public void write(PacketBuffer buffer, SqueezerRecipe recipe) {
            buffer.writeVarInt(recipe.processingTime);
            RecipeSerializers.write(buffer, recipe.resources, PacketBuffer::writeItemStack);
            recipe.fluidOutput.writeToPacket(buffer);
            buffer.writeItemStack(recipe.remnants);
            buffer.writeFloat(recipe.remnantsChance);
        }
    }
}
