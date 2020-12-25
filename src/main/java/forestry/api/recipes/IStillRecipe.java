/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface IStillRecipe extends IForestryRecipe {
    IRecipeType<IStillRecipe> TYPE = RecipeManagers.create("forestry:still");

    /**
     * @return Amount of work cycles required to run through the conversion once.
     */
    int getCyclesPerUnit();

    /**
     * @return FluidStack representing the input liquid.
     */
    FluidStack getInput();

    /**
     * @return FluidStack representing the output liquid.
     */
    FluidStack getOutput();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }

    class Companion {
        @ObjectHolder("forestry:still")
        public static final IRecipeSerializer<IStillRecipe> SERIALIZER = null;
    }
}
