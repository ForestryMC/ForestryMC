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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.utils.ItemStackUtil;

public class FabricatorRecipeManager implements IFabricatorManager {

	private static final Set<IFabricatorRecipe> recipes = new HashSet<>();

	@Override
	public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {
		IFabricatorRecipe recipe = new FabricatorRecipe(plan, molten, ShapedRecipeCustom.createShapedRecipe(result, pattern));
		addRecipe(recipe);
	}

	@Override
	public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
		RecipeManagers.fabricatorSmeltingManager.addSmelting(resource, molten, meltingPoint);
	}

	public static IFabricatorRecipe findMatchingRecipe(ItemStack plan, IInventory resources) {
		ItemStack[][] gridResources = RecipeUtil.getResources(resources);

		for (IFabricatorRecipe recipe : recipes) {
			if (recipe.getPlan() != null && !ItemStackUtil.isCraftingEquivalent(recipe.getPlan(), plan)) {
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

	@Override
	public Map<Object[], Object[]> getRecipes() {
		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (IFabricatorRecipe recipe : recipes) {
			recipeList.put(recipe.getIngredients(), new Object[]{recipe.getRecipeOutput()});
		}

		return recipeList;
	}
}
