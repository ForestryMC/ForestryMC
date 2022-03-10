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

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.IFabricatorRecipe;

public class FabricatorRecipeManager extends AbstractCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {

	public FabricatorRecipeManager() {
		super(IFabricatorRecipe.TYPE);
	}

	@Override
	public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {
		// TODO: json
	}

	@Override
	public Optional<IFabricatorRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, Level world, FluidStack fluidStack, ItemStack plan, Container resources) {
		return getRecipes(recipeManager)
				.filter(recipe ->
						fluidStack.containsFluid(recipe.getLiquid()) &&
						recipe.getPlan().test(plan) &&
						recipe.getCraftingGridRecipe().matches(FakeCraftingInventory.of(resources), world)
				)
				.findFirst();
	}

	@Override
	public boolean isPlan(@Nullable RecipeManager recipeManager, ItemStack plan) {
		return getRecipes(recipeManager)
				.anyMatch(recipe -> recipe.getPlan().test(plan));
	}
}
