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
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

import forestry.api.recipes.ICentrifugeManager;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IForestryRecipe;

public class CentrifugeRecipeManager extends AbstractCraftingProvider<ICentrifugeRecipe> implements ICentrifugeManager {

	public CentrifugeRecipeManager() {
		super(ICentrifugeRecipe.TYPE);
	}

	@Override
	public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {
		NonNullList<ICentrifugeRecipe.Product> list = NonNullList.create();

		for (Map.Entry<ItemStack, Float> entry : products.entrySet()) {
			list.add(new ICentrifugeRecipe.Product(entry.getValue(), entry.getKey()));
		}

		ICentrifugeRecipe recipe = new CentrifugeRecipe(IForestryRecipe.anonymous(), timePerItem, Ingredient.fromStacks(resource), list);
		addRecipe(recipe);
	}

	@Override
	@Nullable
	public ICentrifugeRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return null;
		}

		for (ICentrifugeRecipe recipe : getRecipes(recipeManager)) {
			Ingredient recipeInput = recipe.getInput();
			if (recipeInput.test(itemStack)) {
				return recipe;
			}
		}
		return null;
	}
}
