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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISqueezerContainerManager;
import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.core.fluids.FluidHelper;

public class SqueezerContainerRecipeManager extends AbstractCraftingProvider<ISqueezerContainerRecipe> implements ISqueezerContainerManager {

	public SqueezerContainerRecipeManager() {
		super(ISqueezerContainerRecipe.TYPE);
	}

	@Override
	public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, ItemStack remnants, float chance) {
		addRecipe(new SqueezerContainerRecipe(IForestryRecipe.anonymous(), emptyContainer, timePerItem, remnants, chance));
	}

	@Nullable
	@Override
	public ISqueezerContainerRecipe findMatchingContainerRecipe(@Nullable RecipeManager recipeManager, ItemStack filledContainer) {
		if (!FluidHelper.isDrainableFilledContainer(filledContainer)) {
			return null;
		}

		for (ISqueezerContainerRecipe recipe : getRecipes(recipeManager)) {
			if (recipe.getEmptyContainer().getItem() == filledContainer.getItem()) {
				return recipe;
			}
		}

		return null;
	}
}
