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
import net.minecraft.core.NonNullList;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISqueezerManager;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.utils.ItemStackUtil;

import java.util.Optional;

public class SqueezerRecipeManager extends AbstractCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {

	public SqueezerRecipeManager() {
		super(ISqueezerRecipe.TYPE);
	}

	@Override
	public void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid, ItemStack remnants, int chance) {
		addRecipe(new SqueezerRecipe(IForestryRecipe.anonymous(), timePerItem, resources, liquid, remnants, chance / 100.0f));
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
	public Optional<ISqueezerRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, NonNullList<ItemStack> items) {
		return getRecipes(recipeManager)
				.filter(recipe -> {
					int[] consume = ItemStackUtil.createConsume(recipe.getResources(), items.size(), items::get, false);
					return consume.length > 0;
				})
				.findFirst();
	}

	@Override
	public boolean canUse(@Nullable RecipeManager recipeManager, ItemStack itemStack) {
		return getRecipes(recipeManager)
				.flatMap(recipe -> recipe.getResources().stream())
				.anyMatch(resource -> resource.test(itemStack));
	}
}
