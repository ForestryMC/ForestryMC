/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.factory.recipes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.RecipePair;
import forestry.core.utils.ItemStackUtil;

public class FabricatorRecipeManager extends AbstractCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {

	public FabricatorRecipeManager() {
		super(IFabricatorRecipe.TYPE);
	}

	@Override
	public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {
		//TODO json
		//		ShapedRecipeCustom patternRecipe = new ShapedRecipeCustom(result, pattern);
		//		NonNullList<NonNullList<ItemStack>> ingredients = patternRecipe.getRawIngredients();
		//
		//		IFabricatorRecipe recipe = new FabricatorRecipe(plan, molten, result, ingredients, patternRecipe.getOreDicts(), patternRecipe.getWidth(), patternRecipe.getHeight());
		//		addRecipe(recipe);
	}

	@Override
	public RecipePair<IFabricatorRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack plan, IInventory resources) {
		return RecipePair.EMPTY;
	}

	@Override
	public boolean isPlan(@Nullable RecipeManager recipeManager, ItemStack plan) {
		for (IFabricatorRecipe recipe : getRecipes(recipeManager)) {
			if (ItemStackUtil.isIdenticalItem(recipe.getPlan(), plan)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<IFabricatorRecipe> getRecipesWithOutput(@Nullable RecipeManager recipeManager, ItemStack output) {
		if (output.isEmpty()) {
			return Collections.emptyList();
		}

		return getRecipes(recipeManager).stream().filter(recipe -> {
			ItemStack o = recipe.getRecipeOutput();
			return ItemStackUtil.isIdenticalItem(output, o);
		}).collect(Collectors.toList());
	}
}
