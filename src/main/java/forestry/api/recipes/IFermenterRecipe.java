/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

public interface IFermenterRecipe extends IForestryRecipe, Comparable<IFermenterRecipe> {
    IRecipeType<IFermenterRecipe> TYPE = RecipeManagers.create("forestry:fermenter");

    class Companion {
        @ObjectHolder("forestry:fermenter")
        public static final IRecipeSerializer<IFermenterRecipe> SERIALIZER = null;
    }

    /**
     * @return Ingredient representing the input resource.
     */
    Ingredient getResource();

    /**
     * @return String representing the input resource as a {@link net.minecraftforge.oredict.OreDictionary} name.
     */
    @Nullable
    String getResourceOreName();

    /**
     * @return FluidStack representing the input fluid resource.
     */
    FluidStack getFluidResource();

    /**
     * @return Value of the given resource, i.e. how much needs to be fermented for the output to be deposited into the product tank.
     */
    int getFermentationValue();

    /**
     * @return Modifies the amount of liquid output per work cycle.
     * (water = 1.0f, honey = 1.5f)
     */
    float getModifier();

    /**
     * @return Fluid representing output. Amount is determined by fermentationValue * modifier.
     */
    Fluid getOutput();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }
}
