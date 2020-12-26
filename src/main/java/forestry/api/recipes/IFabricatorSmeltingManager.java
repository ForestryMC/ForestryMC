/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.recipes;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.Set;

public interface IFabricatorSmeltingManager extends ICraftingProvider<IFabricatorSmeltingRecipe> {
    @Nullable
    IFabricatorSmeltingRecipe findMatchingSmelting(RecipeManager manager, ItemStack resource);

    Set<Fluid> getRecipeFluids(RecipeManager manager);
}
