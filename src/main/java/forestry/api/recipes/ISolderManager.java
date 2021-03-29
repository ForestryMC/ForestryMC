/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;

public interface ISolderManager extends ICraftingProvider<ISolderRecipe> {

	void addRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit);

	@Nullable
	ICircuit getCircuit(@Nullable RecipeManager recipeManager, ICircuitLayout layout, ItemStack resource);

	@Nullable
	ISolderRecipe getMatchingRecipe(@Nullable RecipeManager recipeManager, @Nullable ICircuitLayout layout, ItemStack resource);
}
