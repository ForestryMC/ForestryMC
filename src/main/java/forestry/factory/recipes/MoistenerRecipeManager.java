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
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.IMoistenerRecipe;

public class MoistenerRecipeManager implements IMoistenerManager {

	@Override
	public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {
		IMoistenerRecipe recipe = new MoistenerRecipe(IForestryRecipe.anonymous(), Ingredient.fromStacks(resource), product, timePerItem);
		addRecipe(recipe);
	}

	public boolean isResource(ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (IMoistenerRecipe rec : recipes) {
			if (rec.getResource().test(resource)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public IMoistenerRecipe findMatchingRecipe(ItemStack item) {
		for (IMoistenerRecipe recipe : recipes) {
			if (recipe.getResource().test(item)) {
				return recipe;
			}
		}
		return null;
	}

	@Override
	public boolean addRecipe(IMoistenerRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public Collection<IMoistenerRecipe> getRecipes(RecipeManager manager) {
		return ICraftingProvider.findRecipes(manager, IMoistenerRecipe.TYPE);
	}
}
