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
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;

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
		addRecipe(new FermenterRecipe(IForestryRecipe.anonymous(), Ingredient.of(resource), fermentationValue, modifier, output.getFluid(), liquid));
	}

	@Override
	public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {
		addRecipe(resource, fermentationValue, modifier, output, new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME));
	}

	@Override
	public void addRecipe(int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		addRecipe(new FermenterRecipe(IForestryRecipe.anonymous(), fermentationValue, modifier, output.getFluid(), liquid));
	}

	@Override
	public void addRecipe(int fermentationValue, float modifier, FluidStack output) {
		addRecipe(fermentationValue, modifier, output, new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME));
	}

	@Override
	public boolean isResource(@Nullable RecipeManager recipeManager, ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (IFermenterRecipe recipe : getRecipes(recipeManager)) {
			if (recipe.getResource().test(resource)) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Nullable
	public IFermenterRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack res, FluidStack liqu) {
		if (res.isEmpty()) {
			return null;
		}
		for (IFermenterRecipe recipe : getRecipes(recipeManager)) {
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

	@Override
	public Set<ResourceLocation> getRecipeFluidInputs(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager).stream()
				.map(recipe -> recipe.getFluidResource().getFluid().getRegistryName())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<ResourceLocation> getRecipeFluidOutputs(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager).stream()
				.map(recipe -> recipe.getOutput().getRegistryName())
				.collect(Collectors.toSet());
	}
}
