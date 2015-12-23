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

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ItemStackUtil;

public class FermenterRecipeManager implements IFermenterManager {

	private static final Set<IFermenterRecipe> recipes = new HashSet<>();
	public static final Set<Fluid> recipeFluidInputs = new HashSet<>();
	public static final Set<Fluid> recipeFluidOutputs = new HashSet<>();

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		IFermenterRecipe recipe = new FermenterRecipe(resource, fermentationValue, modifier, output.getFluid(), liquid);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {
		addRecipe(resource, fermentationValue, modifier, output, Fluids.WATER.getFluid(1000));
	}

	public static IFermenterRecipe findMatchingRecipe(ItemStack res, FluidStack liqu) {
		for (IFermenterRecipe recipe : recipes) {
			if (matches(recipe, res, liqu)) {
				return recipe;
			}
		}
		return null;
	}

	public static boolean matches(IFermenterRecipe recipe, ItemStack res, FluidStack liqu) {
		ItemStack resource = recipe.getResource();
		if (!ItemStackUtil.isCraftingEquivalent(resource, res)) {
			return false;
		}

		FluidStack fluid = recipe.getFluidResource();
		return liqu != null && liqu.isFluidEqual(fluid);
	}

	public static boolean isResource(ItemStack resource) {
		if (resource == null) {
			return false;
		}

		for (IFermenterRecipe recipe : recipes) {
			if (ItemStackUtil.isCraftingEquivalent(recipe.getResource(), resource)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addRecipe(IFermenterRecipe recipe) {
		FluidStack liquid = recipe.getFluidResource();
		recipeFluidInputs.add(liquid.getFluid());

		Fluid output = recipe.getOutput();
		recipeFluidOutputs.add(output);

		return recipes.add(recipe);
	}

	@Override
	public boolean removeRecipe(IFermenterRecipe recipe) {
		FluidStack liquid = recipe.getFluidResource();
		recipeFluidInputs.remove(liquid.getFluid());

		Fluid output = recipe.getOutput();
		recipeFluidOutputs.remove(output);

		return recipes.remove(recipe);
	}

	@Override
	public Set<IFermenterRecipe> recipes() {
		return Collections.unmodifiableSet(recipes);
	}

	@Override
	public Map<Object[], Object[]> getRecipes() {
		HashMap<Object[], Object[]> recipeList = new HashMap<>();

		for (IFermenterRecipe recipe : recipes) {
			recipeList.put(new Object[]{recipe.getResource(), recipe.getFluidResource()}, new Object[]{recipe.getOutput()});
		}

		return recipeList;
	}
}
