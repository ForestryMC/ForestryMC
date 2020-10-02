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
import java.util.Set;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IFabricatorSmeltingManager;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.IForestryRecipe;

public class FabricatorSmeltingRecipeManager implements IFabricatorSmeltingManager {

	@Nullable
	public IFabricatorSmeltingRecipe findMatchingSmelting(ItemStack resource) {
		if (resource.isEmpty()) {
			return null;
		}

		for (IFabricatorSmeltingRecipe smelting : recipes) {
			if (smelting.getResource().test(resource)) {
				return smelting;
			}
		}

		return null;
	}

	@Override
	public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
		addRecipe(new FabricatorSmeltingRecipe(IForestryRecipe.anonymous(), Ingredient.fromStacks(resource), molten, meltingPoint));
	}

	@Override
	public boolean addRecipe(IFabricatorSmeltingRecipe recipe) {
		return recipes.add(recipe);
	}

	public Set<Fluid> getRecipeFluids() {
		if (recipeFluids.isEmpty()) {
			for (IFabricatorSmeltingRecipe recipe : recipes) {
				FluidStack fluidStack = recipe.getProduct();
				if (!fluidStack.isEmpty()) {
					recipeFluids.add(fluidStack.getFluid());
				}
			}
		}
		return Collections.unmodifiableSet(recipeFluids);
	}

	@Override
	public Collection<IFabricatorSmeltingRecipe> getRecipes(RecipeManager manager) {
		return ICraftingProvider.findRecipes(manager, IFabricatorSmeltingRecipe.TYPE);
	}
}
