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
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.utils.ItemStackUtil;

public class CarpenterRecipeManager extends AbstractCraftingProvider<ICarpenterRecipe> implements ICarpenterManager {

	public CarpenterRecipeManager() {
		super(ICarpenterRecipe.TYPE);
	}

	@Override
	public void addRecipe(ItemStack box, ItemStack product, Object[] materials) {
		addRecipe(5, null, box, product, materials);
	}

	@Override
	public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object[] materials) {
		addRecipe(packagingTime, null, box, product, materials);
	}

	@Override
	public void addRecipe(int packagingTime, @Nullable FluidStack liquid, ItemStack box, ItemStack product, Object[] materials) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<ICarpenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack liquid, ItemStack item, IInventory inventory) {
		for (ICarpenterRecipe recipe : getRecipes(recipeManager)) {
			if (matches(recipe, liquid, item, inventory)) {
				return Optional.of(recipe);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, IInventory craftingInventory) {
		if (recipe == null) {
			return false;
		}

		FluidStack liquid = recipe.getFluidResource();
		if (!liquid.isEmpty()) {
			if (resource.isEmpty() || !resource.containsFluid(liquid)) {
				return false;
			}
		}

		Ingredient box = recipe.getBox();
		if (!box.hasNoMatchingItems() && !box.test(item)) {
			return false;
		}

		ShapedRecipe internal = recipe.getCraftingGridRecipe();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBox(@Nullable RecipeManager recipeManager, ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (ICarpenterRecipe recipe : getRecipes(recipeManager)) {
			Ingredient box = recipe.getBox();
			if (box.test(resource)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<ICarpenterRecipe> getRecipesWithOutput(@Nullable RecipeManager recipeManager, ItemStack output) {
		if (output.isEmpty()) {
			return Collections.emptyList();
		}

		return getRecipes(recipeManager).stream()
				.filter(recipe -> {
					ItemStack o = recipe.getCraftingGridRecipe().getRecipeOutput();
					return ItemStackUtil.isIdenticalItem(o, output);
				})
				.collect(Collectors.toList());
	}

	@Override
	public Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager).stream()
				.map(ICarpenterRecipe::getFluidResource)
				.filter(fluidStack -> !fluidStack.isEmpty())
				.map(fluidStack -> fluidStack.getFluid().getRegistryName())
				.collect(Collectors.toSet());
	}
}
