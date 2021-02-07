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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISqueezerManager;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.utils.ItemStackUtil;

public class SqueezerRecipeManager extends AbstractCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {

	public SqueezerRecipeManager() {
		super(ISqueezerRecipe.TYPE);
	}

	@Override
	public void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid, ItemStack remnants, int chance) {
		ISqueezerRecipe recipe = new SqueezerRecipe(IForestryRecipe.anonymous(), timePerItem, resources, liquid, remnants, chance / 100.0f);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(int timePerItem, Ingredient resource, FluidStack liquid, ItemStack remnants, int chance) {
		NonNullList<Ingredient> resourcesList = NonNullList.create();
		resourcesList.add(resource);
		addRecipe(timePerItem, resourcesList, liquid, remnants, chance);
	}

	@Override
	public void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid) {
		addRecipe(timePerItem, resources, liquid, ItemStack.EMPTY, 0);
	}

	@Override
	public void addRecipe(int timePerItem, Ingredient resource, FluidStack liquid) {
		NonNullList<Ingredient> resourcesList = NonNullList.create();
		resourcesList.add(resource);
		addRecipe(timePerItem, resourcesList, liquid);
	}

	@Override
	@Nullable
	public ISqueezerRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, NonNullList<ItemStack> items) {
		for (ISqueezerRecipe recipe : getRecipes(recipeManager)) {
			if (ItemStackUtil.createConsume(recipe.getResources(), items.size(), items::get, false).length > 0) {
				return recipe;
			}
		}

		return null;
	}

	@Override
	public boolean canUse(@Nullable RecipeManager recipeManager, ItemStack itemStack) {
		for (ISqueezerRecipe recipe : getRecipes(recipeManager)) {
			for (Ingredient recipeInput : recipe.getResources()) {
				if (recipeInput.test(itemStack)) {
					return true;
				}
			}
		}

		return false;
	}
}
