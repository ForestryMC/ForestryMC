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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingManager;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.IForestryRecipe;

public class FabricatorSmeltingRecipeManager extends AbstractCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {

	public FabricatorSmeltingRecipeManager() {
		super(IFabricatorSmeltingRecipe.TYPE);
	}

	@Override
	@Nullable
	public IFabricatorSmeltingRecipe findMatchingSmelting(@Nullable RecipeManager recipeManager, ItemStack resource) {
		if (resource.isEmpty()) {
			return null;
		}

		for (IFabricatorSmeltingRecipe smelting : getRecipes(recipeManager)) {
			if (smelting.getResource().test(resource)) {
				return smelting;
			}
		}

		return null;
	}

	@Override
	public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {
		addRecipe(new FabricatorSmeltingRecipe(IForestryRecipe.anonymous(), Ingredient.of(resource), molten, meltingPoint));
	}

	@Override
	public Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager).stream()
				.map(IFabricatorSmeltingRecipe::getProduct)
				.filter(fluidStack -> !fluidStack.isEmpty())
				.map(fluidStack -> fluidStack.getFluid().getRegistryName())
				.collect(Collectors.toSet());
	}
}
