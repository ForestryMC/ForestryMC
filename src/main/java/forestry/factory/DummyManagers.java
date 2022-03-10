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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

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

public class DummyManagers {

	public static abstract class DummyCraftingProvider<T extends IForestryRecipe> implements ICraftingProvider<T> {
		@Override
		public boolean addRecipe(T recipe) {
			return false;
		}

		@Override
		public Stream<T> getRecipes(@Nullable RecipeManager recipeManager) {
			return Stream.of();
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
		public Optional<ICarpenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack liquid, ItemStack item, Container inventory, Level world) {
			return Optional.empty();
		}

		@Override
		public boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, Container craftingInventory, Level world) {
			return false;
		}

		@Override
		public boolean isBox(@Nullable RecipeManager recipeManager, ItemStack resource) {
			return false;
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

		@Override
		public Optional<ICentrifugeRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack itemStack) {
			return Optional.empty();
		}
	}

	public static class DummyFabricatorManager extends DummyCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {

		@Override
		public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {

		}

		@Override
		public Optional<IFabricatorRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, Level world, FluidStack fluidStack, ItemStack plan, Container resources) {
			return Optional.empty();
		}

		@Override
		public boolean isPlan(@Nullable RecipeManager recipeManager, ItemStack plan) {
			return false;
		}

	}

	public static class DummyFabricatorSmeltingManager extends DummyCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {

		@Override
		public Optional<IFabricatorSmeltingRecipe> findMatchingSmelting(@Nullable RecipeManager recipeManager, ItemStack resource) {
			return Optional.empty();
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

		@Override
		public boolean isResource(@Nullable RecipeManager recipeManager, ItemStack resource) {
			return false;
		}

		@Override
		public Optional<IFermenterRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack res, FluidStack liqu) {
			return Optional.empty();
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

		@Override
		public Optional<IMoistenerRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, ItemStack item) {
			return Optional.empty();
		}
	}

	public static class DummySqueezerManager extends DummyCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {

		@Override
		public void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid, ItemStack remnants, int chance) {
		}

		@Override
		public void addRecipe(int timePerItem, Ingredient resource, FluidStack liquid, ItemStack remnants, int chance) {
		}

		@Override
		public void addRecipe(int timePerItem, NonNullList<Ingredient> resources, FluidStack liquid) {
		}

		@Override
		public void addRecipe(int timePerItem, Ingredient resource, FluidStack liquid) {
		}

		@Override
		public Optional<ISqueezerRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, NonNullList<ItemStack> items) {
			return Optional.empty();
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

		@Override
		public Optional<ISqueezerContainerRecipe> findMatchingContainerRecipe(@Nullable RecipeManager recipeManager, ItemStack filledContainer) {
			return Optional.empty();
		}
	}

	public static class DummyStillManager extends DummyCraftingProvider<IStillRecipe> implements IStillManager {

		@Override
		public void addRecipe(int cyclesPerUnit, FluidStack input, FluidStack output) {

		}

		@Override
		public Optional<IStillRecipe> findMatchingRecipe(@Nullable RecipeManager recipeManager, FluidStack item) {
			return Optional.empty();
		}

		@Override
		public boolean matches(@Nullable IStillRecipe recipe, FluidStack item) {
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
