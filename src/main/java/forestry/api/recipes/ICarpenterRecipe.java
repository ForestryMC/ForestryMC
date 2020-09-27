/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface ICarpenterRecipe extends IForestryRecipe {
    IRecipeType<ICarpenterRecipe> TYPE = RecipeManagers.create("forestry:carpenter");

    class Companion {
        @ObjectHolder("forestry:carpenter")
        public static final IRecipeSerializer<ICarpenterRecipe> SERIALIZER = null;
    }

    /**
     * @return Number of work cycles required to craft the recipe once.
     */
    int getPackagingTime();

    /**
     * @return the crafting grid recipe. The crafting recipe's getRecipeOutput() is used as the ICarpenterRecipe's output.
     */
    ShapedRecipe getCraftingGridRecipe();

    /**
     * @return the box required for this recipe. return empty stack if there is no required box.
     * Examples of boxes are the Forestry cartons and crates.
     */
    Ingredient getBox();

    /**
     * @return the fluid required for this recipe. return null if there is no required fluid.
     */
    FluidStack getFluidResource();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }
}
