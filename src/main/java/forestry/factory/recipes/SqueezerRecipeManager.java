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

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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
	public void addRecipe(int timePerItem, NonNullList<ItemStack> resources, FluidStack liquid, ItemStack remnants, int chance) {
		ISqueezerRecipe recipe = new SqueezerRecipe(timePerItem, resources, liquid, remnants, chance / 100.0f);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(int timePerItem, ItemStack resources, FluidStack liquid, ItemStack remnants, int chance) {
		NonNullList<ItemStack> resourcesList = NonNullList.create();
		resourcesList.add(resources);
		addRecipe(timePerItem, resourcesList, liquid, remnants, chance);
	}

	@Override
	public void addRecipe(int timePerItem, NonNullList<ItemStack> resources, FluidStack liquid) {
		addRecipe(timePerItem, resources, liquid, ItemStack.EMPTY, 0);
	}

	@Override
	public void addRecipe(int timePerItem, ItemStack resources, FluidStack liquid) {
		NonNullList<ItemStack> resourcesList = NonNullList.create();
		resourcesList.add(resources);
		addRecipe(timePerItem, resourcesList, liquid);
	}

	@Override
	public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, ItemStack remnants, float chance) {
		containerRecipes.put(emptyContainer, new SqueezerContainerRecipe(emptyContainer, timePerItem, remnants, chance));
	}

	@Nullable
	public static ISqueezerContainerRecipe findMatchingContainerRecipe(ItemStack filledContainer) {
		if (!FluidHelper.isDrainableFilledContainer(filledContainer)) {
			return null;
		}

		return containerRecipes.get(new ItemStack(filledContainer.getItem()));
	}

	@Nullable
	public static ISqueezerRecipe findMatchingRecipe(NonNullList<ItemStack> items) {
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
}
