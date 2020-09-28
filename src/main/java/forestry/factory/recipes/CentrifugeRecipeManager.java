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
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

import forestry.api.recipes.ICentrifugeManager;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;

public class CentrifugeRecipeManager implements ICentrifugeManager {

	@Override
	public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {
		NonNullList<ICentrifugeRecipe.Product> list = NonNullList.create();

		for (Map.Entry<ItemStack, Float> entry : products.entrySet()) {
			list.add(new ICentrifugeRecipe.Product(entry.getValue(), entry.getKey()));
		}

		ICentrifugeRecipe recipe = new CentrifugeRecipe(IForestryRecipe.anonymous(), timePerItem, Ingredient.fromStacks(resource), list);
		addRecipe(recipe);
	}

	@Nullable
	public ICentrifugeRecipe findMatchingRecipe(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return null;
		}

		for (ICentrifugeRecipe recipe : recipes) {
			Ingredient recipeInput = recipe.getInput();
			if (recipeInput.test(itemStack)) {
				return recipe;
			}
		}
		return null;
	}

	@Override
	public boolean addRecipe(ICentrifugeRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public Collection<ICentrifugeRecipe> getRecipes(RecipeManager manager) {
		return ICraftingProvider.findRecipes(manager, ICentrifugeRecipe.TYPE);
	}
}
