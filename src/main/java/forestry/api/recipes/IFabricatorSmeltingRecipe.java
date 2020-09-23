/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ObjectHolder;

public interface IFabricatorSmeltingRecipe extends IForestryRecipe {
    IRecipeType<IFabricatorSmeltingRecipe> TYPE = RecipeManagers.create("forestry:fabricator_smelting");

    class Companion {
        @ObjectHolder("forestry:fabricator_smelting")
        public static final IRecipeSerializer<IFabricatorSmeltingRecipe> SERIALIZER = null;
    }

    /**
     * @return item to be melted down
     */
    Ingredient getResource();

    /**
     * @return temperature at which the item melts. Glass is 1000, Sand is 3000.
     */
    int getMeltingPoint();

    /**
     * @return resulting fluid
     */
    FluidStack getProduct();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return IFabricatorRecipe.Companion.SERIALIZER;
    }
}
