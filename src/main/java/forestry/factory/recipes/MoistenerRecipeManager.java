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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.IMoistenerRecipe;

import java.util.Optional;

public class MoistenerRecipeManager extends AbstractCraftingProvider<IMoistenerRecipe> implements IMoistenerManager {

	public MoistenerRecipeManager() {
		super(IMoistenerRecipe.TYPE);
	}

	@Override
	public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {
		addRecipe(new MoistenerRecipe(IForestryRecipe.anonymous(), Ingredient.of(resource), product, timePerItem));
	}

	@Override
	public boolean isResource(@Nullable RecipeManager recipeManager, ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		return getRecipes(recipeManager)
				.anyMatch(recipe -> recipe.getResource().test(resource));
	}

	@Override
	public Optional<IMoistenerRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack item) {
		return getRecipes(recipeManager)
				.filter(recipe -> recipe.getResource().test(item))
				.findFirst();
	}
}
