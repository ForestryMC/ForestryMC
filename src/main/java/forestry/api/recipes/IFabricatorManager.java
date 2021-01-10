/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorManager extends ICraftingProvider<IFabricatorRecipe> {

	void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern);

	Optional<IFabricatorRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack plan, IInventory resources);

	boolean isPlan(@Nullable RecipeManager recipeManager, ItemStack plan);

	Collection<IFabricatorRecipe> getRecipesWithOutput(@Nullable RecipeManager recipeManager, ItemStack output);
}
