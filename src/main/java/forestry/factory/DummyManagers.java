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
import java.util.Optional;
import java.util.Set;

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

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
		public Collection<T> getRecipes(RecipeManager manager) {
			return Collections.emptySet();
		}
	}

	public static class DummyCarpenterManager extends DummyCraftingProvider<ICarpenterRecipe> implements ICarpenterManager {
		@Override
		public Optional<ICarpenterRecipe> findMatchingRecipe(RecipeManager recipeManager, FluidStack liquid, ItemStack item, IInventory inventory, World world) {
			return Optional.empty();
		}

		@Override
		public boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, IInventory craftingInventory, World world) {
			return false;
		}

		@Override
		public boolean isBox(RecipeManager manager, ItemStack resource) {
			return false;
		}

		@Override
		public Set<Fluid> getRecipeFluids(RecipeManager manager) {
			return null;
		}

		@Override
		public Collection<ICarpenterRecipe> getRecipes(RecipeManager manager, ItemStack itemStack) {
			return null;
		}
	}

	public static class DummyCentrifugeManager extends DummyCraftingProvider<ICentrifugeRecipe> implements ICentrifugeManager {
		@Override
		public ICentrifugeRecipe findMatchingRecipe(RecipeManager manager, ItemStack itemStack) {
			return null;
		}
	}

	public static class DummyFabricatorManager extends DummyCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {
		@Override
		public Optional<IFabricatorRecipe> findMatchingRecipe(RecipeManager manager, World world, FluidStack fluidStack, ItemStack plan, IInventory resources) {
			return Optional.empty();
		}

		@Override
		public boolean isPlan(RecipeManager manager, ItemStack plan) {
			return false;
		}

		@Override
		public Collection<IFabricatorRecipe> getRecipes(RecipeManager manager, ItemStack itemStack) {
			return null;
		}
	}

	public static class DummyFabricatorSmeltingManager extends DummyCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {
		@Nullable
		@Override
		public IFabricatorSmeltingRecipe findMatchingSmelting(RecipeManager manager, ItemStack resource) {
			return null;
		}

		@Override
		public Set<Fluid> getRecipeFluids(RecipeManager manager) {
			return null;
		}
	}

	public static class DummyFermenterManager extends DummyCraftingProvider<IFermenterRecipe> implements IFermenterManager {
		@Override
		public IFermenterRecipe findMatchingRecipe(RecipeManager manager, ItemStack res, FluidStack fluidStack) {
			return null;
		}

		@Override
		public boolean isResource(RecipeManager manager, ItemStack resource) {
			return false;
		}

		@Override
		public Set<Fluid> getRecipeFluidInputs(RecipeManager manager) {
			return null;
		}

		@Override
		public Set<Fluid> getRecipeFluidOutputs(RecipeManager manager) {
			return null;
		}
	}

	public static class DummyMoistenerManager extends DummyCraftingProvider<IMoistenerRecipe> implements IMoistenerManager {
		@Override
		public boolean isResource(RecipeManager manager, ItemStack resource) {
			return false;
		}

		@Nullable
		@Override
		public IMoistenerRecipe findMatchingRecipe(RecipeManager manager, ItemStack item) {
			return null;
		}
	}

	public static class DummySqueezerManager extends DummyCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {
		@Override
		public ISqueezerRecipe findMatchingRecipe(RecipeManager manager, NonNullList<ItemStack> items) {
			return null;
		}

		@Override
		public boolean canUse(RecipeManager manager, ItemStack itemStack) {
			return false;
		}
	}

	public static class DummyStillManager extends DummyCraftingProvider<IStillRecipe> implements IStillManager {
		@Override
		public IStillRecipe findMatchingRecipe(RecipeManager manager, FluidStack item) {
			return null;
		}

		@Override
		public boolean matches(IStillRecipe recipe, FluidStack item) {
			return false;
		}

		@Override
		public Set<Fluid> getRecipeFluidInputs(RecipeManager manager) {
			return null;
		}

		@Override
		public Set<Fluid> getRecipeFluidOutputs(RecipeManager manager) {
			return null;
		}
	}
}
