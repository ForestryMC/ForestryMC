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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ISqueezerManager;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.fluids.FluidHelper;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.datastructures.ItemStackMap;

public class SqueezerRecipeManager implements ISqueezerManager {

	private static final Set<ISqueezerRecipe> recipes = new HashSet<>();
	public static final ItemStackMap<ISqueezerContainerRecipe> containerRecipes = new ItemStackMap<>();

	@Override
	public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid, @Nullable ItemStack remnants, int chance) {
		ISqueezerRecipe recipe = new SqueezerRecipe(timePerItem, resources, liquid, remnants, chance / 100.0f);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(int timePerItem, ItemStack[] resources, FluidStack liquid) {
		addRecipe(timePerItem, resources, liquid, null, 0);
	}

	@Override
	public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, @Nullable ItemStack remnants, float chance) {
		containerRecipes.put(emptyContainer, new SqueezerContainerRecipe(emptyContainer, timePerItem, remnants, chance));
	}

	@Nullable
	public static ISqueezerContainerRecipe findMatchingContainerRecipe(ItemStack filledContainer) {
		ItemStack emptyContainer = FluidHelper.getEmptyContainer(filledContainer);
		if (emptyContainer == null) {
			return null;
		}

		return containerRecipes.get(emptyContainer);
	}

	public static ISqueezerRecipe findMatchingRecipe(ItemStack[] items) {
		// Find container recipes
		for (ItemStack itemStack : items) {
			ISqueezerContainerRecipe containerRecipe = findMatchingContainerRecipe(itemStack);
			if (containerRecipe != null) {
				ISqueezerRecipe recipe = containerRecipe.getSqueezerRecipe(itemStack);
				if (recipe != null) {
					return recipe;
				}
			}
		}

		for (ISqueezerRecipe recipe : recipes) {
			if (ItemStackUtil.containsSets(recipe.getResources(), items, false, false) > 0) {
				return recipe;
			}
		}

		return null;
	}

	public static ISqueezerRecipe findRecipeWithIngredient(ItemStack ingredient) {
		for (ISqueezerRecipe recipe : recipes) {
			for (ItemStack recipeIngredient : recipe.getResources()) {
				if (ItemStackUtil.isCraftingEquivalent(recipeIngredient, ingredient, false, false)) {
					return recipe;
				}
			}
		}

		return null;
	}

	public static boolean canUse(ItemStack itemStack) {
		for (ISqueezerRecipe recipe : recipes) {
			for (ItemStack recipeInput : recipe.getResources()) {
				if (ItemStackUtil.isCraftingEquivalent(recipeInput, itemStack, true, false)) {
					return true;
				}
			}
		}

		return SqueezerRecipeManager.findMatchingContainerRecipe(itemStack) != null;
	}

	@Override
	public boolean addRecipe(ISqueezerRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(ISqueezerRecipe recipe) {
		return recipes.remove(recipe);
	}

	@Override
	public Set<ISqueezerRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	public Map<Object[], Object[]> getRecipes() {
		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (ISqueezerRecipe recipe : recipes) {
			recipeList.put(recipe.getResources(), new Object[]{recipe.getRemnants(), recipe.getFluidOutput()});
		}

		return recipeList;
	}
}
