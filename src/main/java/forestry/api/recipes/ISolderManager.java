/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.recipes;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;

public interface ISolderManager extends ICraftingProvider<ISolderRecipe> {
    @Nullable
    public ICircuit getCircuit(RecipeManager manager, ICircuitLayout layout, ItemStack resource);

    @Nullable
    public ISolderRecipe getMatchingRecipe(RecipeManager manager, @Nullable ICircuitLayout layout, ItemStack resource);
}
