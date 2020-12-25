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

public interface IHygroregulatorRecipe extends IForestryRecipe {
    IRecipeType<IHygroregulatorRecipe> TYPE = RecipeManagers.create("forestry:hygroregulator");

    /**
     * @return FluidStack containing information on fluid and amount.
     */
    FluidStack getResource();

    /**
     * @return The time between the removal of the fluid from the tank and the actual addition to the alveary climate.
     */
    int getTransferTime();

    /**
     * @return The humidity change that this recipe causes in one work cycle.
     */
    float getHumidChange();

    /**
     * @return The temperature change that this recipe causes in one work cycle.
     */
    float getTempChange();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }

    class Companion {
        @ObjectHolder("forestry:hygroregulator")
        public static final IRecipeSerializer<IHygroregulatorRecipe> SERIALIZER = null;
    }
}
