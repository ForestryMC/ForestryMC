/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;

import java.util.Optional;

public interface ISolderManager extends ICraftingProvider<ISolderRecipe> {

	void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit);

	Optional<ICircuit> getCircuit(@Nullable RecipeManager recipeManager, ICircuitLayout layout, ItemStack resource);

	Optional<ISolderRecipe> getMatchingRecipe(@Nullable RecipeManager recipeManager, @Nullable ICircuitLayout layout, ItemStack resource);
}
