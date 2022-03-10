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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISqueezerContainerManager;
import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.core.fluids.FluidHelper;

import java.util.Optional;

public class SqueezerContainerRecipeManager extends AbstractCraftingProvider<ISqueezerContainerRecipe> implements ISqueezerContainerManager {

	public SqueezerContainerRecipeManager() {
		super(ISqueezerContainerRecipe.TYPE);
	}

	@Override
	public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, ItemStack remnants, float chance) {
		addRecipe(new SqueezerContainerRecipe(IForestryRecipe.anonymous(), emptyContainer, timePerItem, remnants, chance));
	}

	@Override
	public Optional<ISqueezerContainerRecipe> findMatchingContainerRecipe(@Nullable RecipeManager recipeManager, ItemStack filledContainer) {
		if (!FluidHelper.isDrainableFilledContainer(filledContainer)) {
			return Optional.empty();
		}

		return getRecipes(recipeManager)
				.filter(recipe -> recipe.getEmptyContainer().getItem() == filledContainer.getItem())
				.findFirst();
	}
}
