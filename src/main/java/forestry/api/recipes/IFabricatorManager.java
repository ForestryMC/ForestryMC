/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import forestry.core.recipes.RecipePair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public interface IFabricatorManager extends ICraftingProvider<IFabricatorRecipe> {

    void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern);

    public RecipePair<IFabricatorRecipe> findMatchingRecipe(RecipeManager manager, ItemStack plan, IInventory resources);

    public boolean isPlan(RecipeManager manager, ItemStack plan);

    public Collection<IFabricatorRecipe> getRecipes(RecipeManager manager, ItemStack itemStack);
}
