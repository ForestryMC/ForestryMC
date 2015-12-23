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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingManager;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.utils.ItemStackUtil;

public class FabricatorSmeltingRecipeManager implements IFabricatorSmeltingManager {
	public static final Set<IFabricatorSmeltingRecipe> recipes = new HashSet<>();

	public static IFabricatorSmeltingRecipe findMatchingSmelting(ItemStack resource) {
		if (resource == null) {
			return null;
		}

		for (IFabricatorSmeltingRecipe smelting : recipes) {
			if (ItemStackUtil.isCraftingEquivalent(smelting.getResource(), resource)) {
				return smelting;
			}
		}

		return null;
	}

	@Override
	public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
		if (resource == null || molten == null) {
			return;
		}
		addRecipe(new FabricatorSmeltingRecipe(resource, molten, meltingPoint));
	}

	@Override
	public boolean addRecipe(IFabricatorSmeltingRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(IFabricatorSmeltingRecipe recipe) {
		return recipes.remove(recipe);
	}

	@Override
	public Collection<IFabricatorSmeltingRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	public Map<Object[], Object[]> getRecipes() {
		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (IFabricatorSmeltingRecipe recipe : recipes) {
			recipeList.put(new Object[]{recipe.getResource()}, new Object[]{recipe.getProduct()});
		}

		return recipeList;
	}
}
