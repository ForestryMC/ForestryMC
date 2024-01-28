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
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;

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
	}

	@Override
	public Optional<ICarpenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack liquid, ItemStack item, Container inventory, Level world) {
		return getRecipes(recipeManager)
				.filter(recipe -> matches(recipe, liquid, item, inventory, world))
				.findFirst();
	}

	@Override
	public boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, Container craftingInventory, Level world) {
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
		if (!box.isEmpty() && !box.test(item)) {
			return false;
		}

		CraftingRecipe internal = recipe.getCraftingGridRecipe();
		return internal.matches(FakeCraftingInventory.of(craftingInventory), world);
	}

	@Override
	public boolean isBox(@Nullable RecipeManager recipeManager, ItemStack resource) {
		if (resource.isEmpty()) {
			return false;
		}

		return getRecipes(recipeManager)
				.anyMatch(recipe -> {
					Ingredient box = recipe.getBox();
					return box.test(resource);
				});
	}

	@Override
	public Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager) {
		return getRecipes(recipeManager)
				.map(ICarpenterRecipe::getFluidResource)
				.filter(fluidStack -> !fluidStack.isEmpty())
				.map(fluidStack -> RegistryNameFinder.getRegistryName(fluidStack.getFluid()))
				.collect(Collectors.toSet());
	}
}
