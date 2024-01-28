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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import deleteme.RegistryNameFinder;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IForestryRecipe;
import net.minecraftforge.fluids.FluidType;

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
		addRecipe(resource, fermentationValue, modifier, output, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));
	}

	@Override
	public void addRecipe(int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {
		addRecipe(new FermenterRecipe(IForestryRecipe.anonymous(), fermentationValue, modifier, output.getFluid(), liquid));
	}

	@Override
	public void addRecipe(int fermentationValue, float modifier, FluidStack output) {
		addRecipe(fermentationValue, modifier, output, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));
	}

	@Override
	public boolean isResource(@Nullable RecipeManager recipeManager, ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		return getRecipes(recipeManager)
				.anyMatch(recipe -> recipe.getResource().test(resource));
	}

	@Override
	public Optional<IFermenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack res, FluidStack liqu) {
		if (res.isEmpty()) {
			return Optional.empty();
		}
		return getRecipes(recipeManager)
				.filter(recipe -> matches(recipe, res, liqu))
				.findFirst();
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
		return getRecipes(recipeManager)
				.map(recipe -> RegistryNameFinder.getRegistryName(recipe.getFluidResource().getFluid()))
				.collect(Collectors.toSet());
	}

	@Override
	public Set<ResourceLocation> getRecipeFluidOutputs(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager)
				.map(recipe -> RegistryNameFinder.getRegistryName(recipe.getOutput()))
				.collect(Collectors.toSet());
	}
}
