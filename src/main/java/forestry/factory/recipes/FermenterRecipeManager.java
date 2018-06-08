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
import java.util.TreeSet;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.core.utils.ItemStackUtil;

public class FermenterRecipeManager implements IFermenterManager {

	private static final Set<IFermenterRecipe> recipes = new TreeSet<>();
	public static final Set<Fluid> recipeFluidInputs = new HashSet<>();
	public static final Set<Fluid> recipeFluidOutputs = new HashSet<>();

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		IFermenterRecipe recipe = new FermenterRecipe(resource, fermentationValue, modifier, output.getFluid(), liquid);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {
		addRecipe(resource, fermentationValue, modifier, output, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
	}

	@Override
	public void addRecipe(String resourceOreName, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		IFermenterRecipe recipe = new FermenterRecipe(resourceOreName, fermentationValue, modifier, output.getFluid(), liquid);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(String resourceOreName, int fermentationValue, float modifier, FluidStack output) {
		addRecipe(resourceOreName, fermentationValue, modifier, output, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME));
	}

	@Nullable
	public static IFermenterRecipe findMatchingRecipe(ItemStack res, FluidStack liqu) {
		if (res.isEmpty()) {
			return null;
		}
		for (IFermenterRecipe recipe : recipes) {
			if (matches(recipe, res, liqu)) {
				return recipe;
			}
		}
		return null;
	}

	public static boolean matches(IFermenterRecipe recipe, ItemStack res, FluidStack liqu) {
		ItemStack resource = recipe.getResource();
		if (!ItemStackUtil.isCraftingEquivalent(resource, res, recipe.getResourceOreName(), false)) {
			return false;
		}

		FluidStack fluid = recipe.getFluidResource();
		return liqu.isFluidEqual(fluid);
	}

	public static boolean isResource(ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (IFermenterRecipe recipe : recipes) {
			if (ItemStackUtil.isCraftingEquivalent(recipe.getResource(), resource, recipe.getResourceOreName(), false)) {
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
}
