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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.utils.ItemStackUtil;

public class FabricatorRecipeManager extends AbstractCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {
	public FabricatorRecipeManager() {
		super(IFabricatorRecipe.TYPE);
	}

	public Optional<IFabricatorRecipe> findMatchingRecipe(RecipeManager manager, World world, FluidStack fluidStack, ItemStack plan, IInventory resources) {
		for (IFabricatorRecipe recipe : getRecipes(manager)) {
			if (fluidStack.containsFluid(recipe.getLiquid()) && recipe.getPlan().test(plan) && recipe
					.getCraftingGridRecipe().matches(FakeCraftingInventory.of(resources), world)) {
				return Optional.of(recipe);
			}
		}

		return Optional.empty();
	}

	public boolean isPlan(RecipeManager manager, ItemStack plan) {
		for (IFabricatorRecipe recipe : getRecipes(manager)) {
			if (recipe.getPlan().test(plan)) {
				return true;
			}
		}

		return false;
	}

	public Collection<IFabricatorRecipe> getRecipes(RecipeManager manager, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return Collections.emptyList();
		}

		return getRecipes(manager).stream().filter(recipe -> {
			ItemStack output = recipe.getCraftingGridRecipe().getRecipeOutput();
			return ItemStackUtil.isIdenticalItem(itemStack, output);
		}).collect(Collectors.toList());
	}
}
