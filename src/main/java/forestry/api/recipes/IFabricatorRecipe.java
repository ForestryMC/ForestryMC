/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface IFabricatorRecipe extends IForestryRecipe {
    IRecipeType<IFabricatorRecipe> TYPE = RecipeManagers.create("forestry:fabricator");

    /**
     * @return the molten liquid (and amount) required for this recipe.
     */
    FluidStack getLiquid();

    /**
     * @return the crafting grid recipe. The crafting recipe's getRecipeOutput() is used as the ICarpenterRecipe's output.
     */
    ShapedRecipe getCraftingGridRecipe();

    /**
     * @return the plan for this recipe (the item in the top right slot). may be an empty ItemStack
     */
    ItemStack getPlan();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }

    class Companion {
        @ObjectHolder("forestry:fabricator")
        public static final IRecipeSerializer<IFabricatorRecipe> SERIALIZER = null;
    }
}
