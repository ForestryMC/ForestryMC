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

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IForestryRecipe;

public class FermenterRecipeManager extends AbstractCraftingProvider<IFermenterRecipe> implements IFermenterManager {

	public FermenterRecipeManager() {
		super(IFermenterRecipe.TYPE);
	}

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		IFermenterRecipe recipe = new FermenterRecipe(IForestryRecipe.anonymous(), Ingredient.fromStacks(resource), fermentationValue, modifier, output.getFluid(), liquid);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {
		addRecipe(resource, fermentationValue, modifier, output, new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME));
	}

	@Override
	public void addRecipe(int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		IFermenterRecipe recipe = new FermenterRecipe(IForestryRecipe.anonymous(), fermentationValue, modifier, output.getFluid(), liquid);
		addRecipe(recipe);
	}

	@Override
	public void addRecipe(int fermentationValue, float modifier, FluidStack output) {
		addRecipe(fermentationValue, modifier, output, new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME));
	}

	@Nullable
	public IFermenterRecipe findMatchingRecipe(ItemStack res, FluidStack liqu) {
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

	public boolean matches(IFermenterRecipe recipe, ItemStack res, FluidStack liqu) {
		Ingredient resource = recipe.getResource();
		if (!resource.test(res)) {
			return false;
		}

		FluidStack fluid = recipe.getFluidResource();
		return liqu.isFluidEqual(fluid);
	}

	public boolean isResource(ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (IFermenterRecipe recipe : recipes) {
			if (recipe.getResource().test(resource)) {
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

		return super.addRecipe(recipe);
	}
}
