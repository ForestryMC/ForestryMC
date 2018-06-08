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

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillManager;
import forestry.api.recipes.IStillRecipe;

public class StillRecipeManager implements IStillManager {

	private static final Set<IStillRecipe> recipes = new HashSet<>();
	public static final Set<Fluid> recipeFluidInputs = new HashSet<>();
	public static final Set<Fluid> recipeFluidOutputs = new HashSet<>();

	@Override
	public void addRecipe(int timePerUnit, FluidStack input, FluidStack output) {
		IStillRecipe recipe = new StillRecipe(timePerUnit, input, output);
		addRecipe(recipe);
	}

	@Nullable
	public static IStillRecipe findMatchingRecipe(@Nullable FluidStack item) {
		if (item == null) {
			return null;
		}
		for (IStillRecipe recipe : recipes) {
			if (matches(recipe, item)) {
				return recipe;
			}
		}
		return null;
	}

	public static boolean matches(@Nullable IStillRecipe recipe, @Nullable FluidStack item) {
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

		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(IStillRecipe recipe) {
		FluidStack input = recipe.getInput();
		recipeFluidInputs.remove(input.getFluid());

		FluidStack output = recipe.getOutput();
		recipeFluidOutputs.remove(output.getFluid());

		return recipes.remove(recipe);
	}

	@Override
	public Set<IStillRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}
}
