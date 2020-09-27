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
import forestry.api.recipes.IStillRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class StillRecipe implements IStillRecipe {
    private final ResourceLocation id;
    private final int timePerUnit;
    private final FluidStack input;
    private final FluidStack output;

    public StillRecipe(ResourceLocation id, int timePerUnit, FluidStack input, FluidStack output) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(input, "Still recipes need an input. Input was null.");
        Preconditions.checkNotNull(output, "Still recipes need an output. Output was null.");

        this.id = id;
        this.timePerUnit = timePerUnit;
        this.input = input;
        this.output = output;
    }

    @Override
    public int getCyclesPerUnit() {
        return timePerUnit;
    }

    @Override
    public FluidStack getInput() {
        return input;
    }

    @Override
    public FluidStack getOutput() {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<StillRecipe> {
        @Override
        public StillRecipe read(ResourceLocation recipeId, JsonObject json) {
            int timePerUnit = JSONUtils.getInt(json, "time");
            FluidStack input = RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "input"));
            FluidStack output = RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "output"));

            return new StillRecipe(recipeId, timePerUnit, input, output);
        }

        @Override
        public StillRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int timePerUnit = buffer.readVarInt();
            FluidStack input = FluidStack.readFromPacket(buffer);
            FluidStack output = FluidStack.readFromPacket(buffer);

            return new StillRecipe(recipeId, timePerUnit, input, output);
        }

        @Override
        public void write(PacketBuffer buffer, StillRecipe recipe) {
            buffer.writeVarInt(recipe.timePerUnit);
            recipe.input.writeToPacket(buffer);
            recipe.output.writeToPacket(buffer);
        }
    }
}
