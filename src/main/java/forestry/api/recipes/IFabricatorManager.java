/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

public interface IFabricatorManager extends ICraftingProvider<IFabricatorRecipe> {
	Optional<IFabricatorRecipe> findMatchingRecipe(RecipeManager manager, ItemStack plan, IInventory resources);

	boolean isPlan(RecipeManager manager, ItemStack plan);

	Collection<IFabricatorRecipe> getRecipes(RecipeManager manager, ItemStack itemStack);
}
