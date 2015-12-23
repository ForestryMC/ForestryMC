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

import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.utils.ItemStackUtil;

public class MoistenerRecipeManager implements IMoistenerManager {

	private static final Set<IMoistenerRecipe> recipes = new HashSet<>();

	@Override
	public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {
		IMoistenerRecipe recipe = new MoistenerRecipe(resource, product, timePerItem);
		addRecipe(recipe);
	}

	public static boolean isResource(ItemStack resource) {
		if (resource == null) {
			return false;
		}

		for (IMoistenerRecipe rec : recipes) {
			if (ItemStackUtil.isIdenticalItem(resource, rec.getResource())) {
				return true;
			}
		}

		return false;
	}

	public static IMoistenerRecipe findMatchingRecipe(ItemStack item) {
		for (IMoistenerRecipe recipe : recipes) {
			if (ItemStackUtil.isCraftingEquivalent(recipe.getResource(), item)) {
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
	public boolean removeRecipe(IMoistenerRecipe recipe) {
		return recipes.remove(recipe);
	}

	@Override
	public Set<IMoistenerRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	public Map<Object[], Object[]> getRecipes() {
		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (IMoistenerRecipe recipe : recipes) {
			recipeList.put(new ItemStack[]{recipe.getResource()}, new ItemStack[]{recipe.getProduct()});
		}

		return recipeList;
	}
}
