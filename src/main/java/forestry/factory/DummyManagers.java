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
package forestry.factory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeManager;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IFabricatorManager;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingManager;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.ISqueezerContainerManager;
import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.api.recipes.ISqueezerManager;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillManager;
import forestry.api.recipes.IStillRecipe;
import forestry.core.recipes.RecipePair;

public class DummyManagers {

	public static abstract class DummyCraftingProvider<T extends IForestryRecipe> implements ICraftingProvider<T> {
		@Override
		public boolean addRecipe(T recipe) {
			return false;
		}

		@Override
		public Collection<T> getRecipes(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}
	}

	public static class DummyCarpenterManager extends DummyCraftingProvider<ICarpenterRecipe> implements ICarpenterManager {

		@Override
		public void addRecipe(ItemStack box, ItemStack product, Object... materials) {

		}

		@Override
		public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object... materials) {

		}

		@Override
		public void addRecipe(int packagingTime, @Nullable FluidStack liquid, ItemStack box, ItemStack product, Object... materials) {

		}

		@Override
		public Optional<ICarpenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack liquid, ItemStack item, IInventory inventory) {
			return Optional.empty();
		}

		@Override
		public boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, IInventory craftingInventory) {
			return false;
		}

		@Override
		public boolean isBox(@Nullable RecipeManager recipeManager, ItemStack resource) {
			return false;
		}

		@Override
		public Collection<ICarpenterRecipe> getRecipesWithOutput(@Nullable RecipeManager recipeManager, ItemStack output) {
			return Collections.emptySet();
		}

		@Override
		public Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}
	}

	public static class DummyCentrifugeManager extends DummyCraftingProvider<ICentrifugeRecipe> implements ICentrifugeManager {

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {

		}

		@Nullable
		@Override
		public ICentrifugeRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack itemStack) {
			return null;
		}
	}

	public static class DummyFabricatorManager extends DummyCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {

		@Override
		public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {

		}

		@Override
		public RecipePair<IFabricatorRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack plan, IInventory resources) {
			return RecipePair.EMPTY;
		}

		@Override
		public boolean isPlan(@Nullable RecipeManager recipeManager, ItemStack plan) {
			return false;
		}

		@Override
		public Collection<IFabricatorRecipe> getRecipesWithOutput(@Nullable RecipeManager recipeManager, ItemStack output) {
			return Collections.emptySet();
		}

	}

	public static class DummyFabricatorSmeltingManager extends DummyCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {

		@Nullable
		@Override
		public IFabricatorSmeltingRecipe findMatchingSmelting(@Nullable RecipeManager recipeManager, ItemStack resource) {
			return null;
		}

		@Override
		public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {

		}

		@Override
		public Set<ResourceLocation> getRecipeFluids(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}
	}

	public static class DummyFermenterManager extends DummyCraftingProvider<IFermenterRecipe> implements IFermenterManager {

		@Override
		public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {

		}

		@Override
		public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {

		}

		@Override
		public void addRecipe(int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {

		}

		@Override
		public void addRecipe(int fermentationValue, float modifier, FluidStack output) {

		}

		@Nullable
		@Override
		public IFermenterRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack res, FluidStack liqu) {
			return null;
		}

		@Override
		public Set<ResourceLocation> getRecipeFluidInputs(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}

		@Override
		public Set<ResourceLocation> getRecipeFluidOutputs(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}
	}

	public static class DummyMoistenerManager extends DummyCraftingProvider<IMoistenerRecipe> implements IMoistenerManager {

		@Override
		public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {

		}

		@Override
		public boolean isResource(@Nullable RecipeManager recipeManager, ItemStack resource) {
			return false;
		}

		@Nullable
		@Override
		public IMoistenerRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack item) {
			return null;
		}
	}

	public static class DummySqueezerManager extends DummyCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {

		@Override
		public void addRecipe(int timePerItem, NonNullList<ItemStack> resources, FluidStack liquid, ItemStack remnants, int chance) {

		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resources, FluidStack liquid, ItemStack remnants, int chance) {

		}

		@Override
		public void addRecipe(int timePerItem, NonNullList<ItemStack> resources, FluidStack liquid) {

		}

		@Override
		public void addRecipe(int timePerItem, ItemStack resources, FluidStack liquid) {

		}

		@Nullable
		@Override
		public ISqueezerRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, NonNullList<ItemStack> items) {
			return null;
		}

		@Override
		public boolean canUse(@Nullable RecipeManager recipeManager, ItemStack itemStack) {
			return false;
		}
	}

	public static class DummySqueezerContainerManager extends DummyCraftingProvider<ISqueezerContainerRecipe> implements ISqueezerContainerManager {

		@Override
		public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, ItemStack remnants, float chance) {

		}

		@Nullable
		@Override
		public ISqueezerContainerRecipe findMatchingContainerRecipe(@Nullable RecipeManager recipeManager, ItemStack filledContainer) {
			return null;
		}
	}

	public static class DummyStillManager extends DummyCraftingProvider<IStillRecipe> implements IStillManager {

		@Override
		public void addRecipe(int cyclesPerUnit, FluidStack input, FluidStack output) {

		}

		@Override
		public IStillRecipe findMatchingRecipe(@Nullable RecipeManager recipeManager, @Nullable FluidStack item) {
			return null;
		}

		@Override
		public boolean matches(@Nullable IStillRecipe recipe, @Nullable FluidStack item) {
			return false;
		}

		@Override
		public Set<ResourceLocation> getRecipeFluidInputs(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}

		@Override
		public Set<ResourceLocation> getRecipeFluidOutputs(@Nullable RecipeManager recipeManager) {
			return Collections.emptySet();
		}
	}
}
