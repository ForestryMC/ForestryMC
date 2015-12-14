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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.utils.ItemStackUtil;

public class CarpenterRecipeManager implements ICarpenterManager {

	private static final Set<ICarpenterRecipe> recipes = new HashSet<>();
	private static final Set<Fluid> recipeFluids = new HashSet<>();

	@Override
	public void addRecipe(ItemStack box, ItemStack product, Object materials[]) {
		addRecipe(5, null, box, product, materials);
	}

	@Override
	public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object materials[]) {
		addRecipe(packagingTime, null, box, product, materials);
	}

	@Override
	public void addRecipe(int packagingTime, FluidStack liquid, ItemStack box, ItemStack product, Object materials[]) {
		ICarpenterRecipe recipe = new CarpenterRecipe(packagingTime, liquid, box, ShapedRecipeCustom.createShapedRecipe(product, materials));
		addRecipe(recipe);
	}

	public static ICarpenterRecipe findMatchingRecipe(FluidStack liquid, ItemStack item, IInventory inventorycrafting) {
		for (ICarpenterRecipe recipe : recipes) {
			if (matches(recipe, liquid, item, inventorycrafting)) {
				return recipe;
			}
		}
		return null;
	}

	public static boolean matches(ICarpenterRecipe recipe, FluidStack resource, ItemStack item, IInventory inventoryCrafting) {
		if (recipe == null) {
			return false;
		}

		FluidStack liquid = recipe.getFluidResource();
		if (liquid != null) {
			if (resource == null || !resource.containsFluid(liquid)) {
				return false;
			}
		}

		ItemStack box = recipe.getBox();
		if (box != null && !ItemStackUtil.isCraftingEquivalent(box, item)) {
			return false;
		}

		IDescriptiveRecipe internal = recipe.getCraftingGridRecipe();
		return RecipeUtil.matches(internal, inventoryCrafting);
	}

	public static boolean isBox(ItemStack resource) {
		if (resource == null) {
			return false;
		}

		for (ICarpenterRecipe recipe : recipes) {
			ItemStack box = recipe.getBox();
			if (ItemStackUtil.isIdenticalItem(box, resource)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addRecipe(ICarpenterRecipe recipe) {
		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(ICarpenterRecipe recipe) {
		boolean removed = recipes.remove(recipe);
		if (removed) {
			recipeFluids.clear();
		}
		return removed;
	}

	public static Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (ICarpenterRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getFluidResource();
				if (fluidStack != null) {
					recipeFluids.add(fluidStack.getFluid());
				}
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}

	@Override
	public Set<ICarpenterRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	public Map<Object[], Object[]> getRecipes() {

		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (ICarpenterRecipe recipe : recipes) {
			recipeList.put(recipe.getCraftingGridRecipe().getIngredients(), new Object[]{recipe.getCraftingGridRecipe().getRecipeOutput()});
		}

		return recipeList;
	}
}
