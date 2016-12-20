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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class FabricatorRecipeManager implements IFabricatorManager {

	private static final Set<IFabricatorRecipe> recipes = new HashSet<>();

	@Override
	public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {
		ShapedRecipeCustom patternRecipe = new ShapedRecipeCustom(result, pattern);
		NonNullList<NonNullList<ItemStack>> ingredients = patternRecipe.getIngredients();

		IFabricatorRecipe recipe = new FabricatorRecipe(plan, molten, result, ingredients);
		addRecipe(recipe);
	}

	@Nullable
	public static IFabricatorRecipe findMatchingRecipe(ItemStack plan, IInventory resources) {
		ItemStack[][] gridResources = RecipeUtil.getResources(resources);

		for (IFabricatorRecipe recipe : recipes) {
			if (!recipe.getPlan().isEmpty() && !ItemStackUtil.isCraftingEquivalent(recipe.getPlan(), plan)) {
				continue;
			}
			if (RecipeUtil.matches(recipe.getIngredients(), recipe.getWidth(), recipe.getHeight(), gridResources)) {
				return recipe;
			}
		}

		return null;
	}

	public static boolean isPlan(ItemStack plan) {
		for (IFabricatorRecipe recipe : recipes) {
			if (ItemStackUtil.isIdenticalItem(recipe.getPlan(), plan)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addRecipe(IFabricatorRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(IFabricatorRecipe recipe) {
		return recipes.remove(recipe);
	}

	@Override
	public Set<IFabricatorRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}
}
