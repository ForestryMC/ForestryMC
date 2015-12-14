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

import net.minecraft.item.ItemStack;

import forestry.api.recipes.ICentrifugeManager;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.utils.ItemStackUtil;

public class CentrifugeRecipeManager implements ICentrifugeManager {

	private static final Set<ICentrifugeRecipe> recipes = new HashSet<>();

	@Override
	public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {
		ICentrifugeRecipe recipe = new CentrifugeRecipe(timePerItem, resource, products);
		addRecipe(recipe);
	}

	public static ICentrifugeRecipe findMatchingRecipe(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}

		for (ICentrifugeRecipe recipe : recipes) {
			ItemStack recipeInput = recipe.getInput();
			if (ItemStackUtil.isCraftingEquivalent(recipeInput, itemStack)) {
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
	public boolean removeRecipe(ICentrifugeRecipe recipe) {
		return recipes.remove(recipe);
	}

	@Override
	public Set<ICentrifugeRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	@Deprecated
	public Map<Object[], Object[]> getRecipes() {
		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (ICentrifugeRecipe recipe : recipes) {
			Set<ItemStack> productsKeys = recipe.getAllProducts().keySet();
			recipeList.put(new Object[]{recipe.getInput()}, productsKeys.toArray(new ItemStack[productsKeys.size()]));
		}

		return recipeList;
	}
}
