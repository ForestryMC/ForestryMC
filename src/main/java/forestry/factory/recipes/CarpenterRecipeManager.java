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

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICraftingProvider;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;

public class CarpenterRecipeManager implements ICarpenterManager {

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
		//		ICarpenterRecipe recipe = new CarpenterRecipe(packagingTime, liquid, box, ShapedRecipeCustom.createShapedRecipe(product, materials));
		//		addRecipe(recipe);
		//TODO json
	}

	@Override
	public Optional<ICarpenterRecipe> findMatchingRecipe(RecipeManager recipeManager, FluidStack liquid, ItemStack item, IInventory inventory) {
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
		return RecipeUtil.matches(internal, craftingInventory) != null;
	}

	public boolean isBox(ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		for (ICarpenterRecipe recipe : recipes) {
			Ingredient box = recipe.getBox();
			if (box.test(resource)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addRecipe(ICarpenterRecipe recipe) {
		return recipes.add(recipe);
	}

	public Collection<ICarpenterRecipe> getRecipes(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return Collections.emptyList();
		}

		return recipes.stream().filter(recipe -> {
			ItemStack output = recipe.getCraftingGridRecipe().getRecipeOutput();
			return ItemStackUtil.isIdenticalItem(itemStack, output);
		}).collect(Collectors.toList());
	}

	public Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (ICarpenterRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getFluidResource();
				if (!fluidStack.isEmpty()) {
					recipeFluids.add(fluidStack.getFluid());
				}
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}

	@Override
	public Collection<ICarpenterRecipe> getRecipes(RecipeManager manager) {
		return ICraftingProvider.findRecipes(manager, ICarpenterRecipe.TYPE);
	}
}
