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

import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IStillManager;
import forestry.api.recipes.IStillRecipe;

public class StillRecipeManager extends AbstractCraftingProvider<IStillRecipe> implements IStillManager {

	public StillRecipeManager() {
		super(IStillRecipe.TYPE);
	}

	@Override
	public void addRecipe(int timePerUnit, FluidStack input, FluidStack output) {
		IStillRecipe recipe = new StillRecipe(IForestryRecipe.anonymous(), timePerUnit, input, output);
		addRecipe(recipe);
	}

	@Override
	@Nullable
	public IStillRecipe findMatchingRecipe(RecipeManager manager, @Nullable FluidStack item) {
		if (item == null) {
			return null;
		}
		for (IStillRecipe recipe : getRecipes(manager)) {
			if (matches(recipe, item)) {
				return recipe;
			}
		}
		return null;
	}

	@Override
	public boolean matches(@Nullable IStillRecipe recipe, @Nullable FluidStack item) {
		if (recipe == null || item == null) {
			return false;
		}

		return item.containsFluid(recipe.getInput());
	}

	@Override
	public boolean addRecipe(IStillRecipe recipe) {
		FluidStack input = recipe.getInput();
		recipeFluidInputs.add(input.getFluid());

		FluidStack output = recipe.getOutput();
		recipeFluidOutputs.add(output.getFluid());

		return super.addRecipe(recipe);
	}
}
