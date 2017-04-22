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

import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;

public class MoistenerRecipeManager implements IMoistenerManager {

	private static final Set<IMoistenerRecipe> recipes = new HashSet<>();

	@Override
	public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {
		IMoistenerRecipe recipe = new MoistenerRecipe(resource, product, timePerItem);
		addRecipe(recipe);
	}

	public static boolean isResource(ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (IMoistenerRecipe rec : recipes) {
			if (ItemStackUtil.isIdenticalItem(resource, rec.getResource())) {
				return true;
			}
		}

		return false;
	}

	@Nullable
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
}
