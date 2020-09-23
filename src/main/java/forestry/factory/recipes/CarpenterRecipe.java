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
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CarpenterRecipe implements ICarpenterRecipe {
    private final ResourceLocation id;
    private final int packagingTime;
    private final FluidStack liquid;
    private final Ingredient box;
    private final ShapedRecipeCustom internal;

    public CarpenterRecipe(
            ResourceLocation id,
            int packagingTime,
            FluidStack liquid,
            Ingredient box,
            ShapedRecipeCustom internal
    ) {
        Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
        Preconditions.checkNotNull(box);
        Preconditions.checkNotNull(internal);

        this.id = id;
        this.packagingTime = packagingTime;
        this.liquid = liquid;
        this.box = box;
        this.internal = internal;
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
    public IDescriptiveRecipe getCraftingGridRecipe() {
        return internal;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    // TODO: Remove ShapedRecipeCustom. We cannot serialize this without ShapedRecipeCustom having a serializer
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CarpenterRecipe> {
        @Override
        public CarpenterRecipe read(ResourceLocation recipeId, JsonObject json) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CarpenterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(PacketBuffer buffer, CarpenterRecipe recipe) {
            throw new UnsupportedOperationException();
        }
    }
}
