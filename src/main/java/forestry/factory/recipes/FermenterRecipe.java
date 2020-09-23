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
import forestry.api.recipes.IFermenterRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class FermenterRecipe implements IFermenterRecipe {
    private final ResourceLocation id;
    private final Ingredient resource;
    @Nullable
    private final String resourceOreName;
    private final int fermentationValue;
    private final float modifier;
    private final Fluid output;
    private final FluidStack fluidResource;

    public FermenterRecipe(
            ResourceLocation id,
            Ingredient resource,
            int fermentationValue,
            float modifier,
            Fluid output,
            FluidStack fluidResource
    ) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(resource, "Fermenter Resource cannot be null!");
        Preconditions.checkArgument(!resource.hasNoMatchingItems(), "Fermenter Resource item cannot be empty!");
        Preconditions.checkNotNull(output, "Fermenter Output cannot be null!");
        Preconditions.checkNotNull(fluidResource, "Fermenter Liquid cannot be null!");

        this.id = id;
        this.resource = resource;
        this.resourceOreName = null;
        this.fermentationValue = fermentationValue;
        this.modifier = modifier;
        this.output = output;
        this.fluidResource = fluidResource;
    }

    public FermenterRecipe(
            ResourceLocation id,
            String resourceOreName,
            int fermentationValue,
            float modifier,
            Fluid output,
            FluidStack fluidResource
    ) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(resourceOreName, "Fermenter Resource cannot be null!");
        Preconditions.checkArgument(!resourceOreName.isEmpty(), "Fermenter Resource ore name cannot be empty!");
        Preconditions.checkNotNull(output, "Fermenter Output cannot be null!");
        Preconditions.checkNotNull(fluidResource, "Fermenter Liquid cannot be null!");

        this.id = id;
        this.resource = Ingredient.EMPTY;
        this.resourceOreName = resourceOreName;
        this.fermentationValue = fermentationValue;
        this.modifier = modifier;
        this.output = output;
        this.fluidResource = fluidResource;
    }


    @Override
    public Ingredient getResource() {
        return resource;
    }

    @Nullable
    @Override
    public String getResourceOreName() {
        return resourceOreName;
    }

    @Override
    public FluidStack getFluidResource() {
        return fluidResource;
    }

    @Override
    public int getFermentationValue() {
        return fermentationValue;
    }

    @Override
    public float getModifier() {
        return modifier;
    }

    @Override
    public Fluid getOutput() {
        return output;
    }

    @Override
    public int compareTo(IFermenterRecipe o) {
        return !resource.hasNoMatchingItems() ? -1 : 1;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FermenterRecipe> {
        @Override
        public FermenterRecipe read(ResourceLocation recipeId, JsonObject json) {
            Ingredient resource = Ingredient.deserialize(json.get("resource"));
            int fermentationValue = JSONUtils.getInt(json, "fermentationValue");
            float modifier = JSONUtils.getFloat(json, "modifier");
            Fluid output = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(JSONUtils.getString(json, "output")));
            FluidStack fluidResource = RecipeSerializers.load(JSONUtils.getJsonObject(json, "fluidResource"));

            return new FermenterRecipe(recipeId, resource, fermentationValue, modifier, output, fluidResource);
        }

        @Override
        public FermenterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient resource = Ingredient.read(buffer);
            int fermentationValue = buffer.readVarInt();
            float modifier = buffer.readFloat();
            Fluid output = ForgeRegistries.FLUIDS.getValue(buffer.readResourceLocation());
            FluidStack fluidResource = FluidStack.readFromPacket(buffer);

            return new FermenterRecipe(recipeId, resource, fermentationValue, modifier, output, fluidResource);
        }

        @Override
        public void write(PacketBuffer buffer, FermenterRecipe recipe) {
            recipe.resource.write(buffer);
            buffer.writeVarInt(recipe.fermentationValue);
            buffer.writeFloat(recipe.modifier);
            buffer.writeResourceLocation(ForgeRegistries.FLUIDS.getKey(recipe.output));
            recipe.fluidResource.writeToPacket(buffer);
        }
    }
}
