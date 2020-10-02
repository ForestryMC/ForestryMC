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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

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
		public void addRecipe(ItemStack box, ItemStack product, Object... materials) {

		}

		@Override
		public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object... materials) {

		}

		@Override
		public void addRecipe(int packagingTime, @Nullable FluidStack liquid, ItemStack box, ItemStack product, Object... materials) {

		}

		@Override
		public Optional<ICarpenterRecipe> findMatchingRecipe(RecipeManager recipeManager, FluidStack liquid, ItemStack item, IInventory inventory) {
			return Optional.empty();
		}

		@Override
		public boolean matches(@Nullable ICarpenterRecipe recipe, FluidStack resource, ItemStack item, IInventory craftingInventory) {
			return false;
		}
	}

	public static class DummyCentrifugeManager extends DummyCraftingProvider<ICentrifugeRecipe> implements ICentrifugeManager {

		@Override
		public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {

		}
	}

	public static class DummyFabricatorManager extends DummyCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {

		@Override
		public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {

		}

	}

	public static class DummyFabricatorSmeltingManager extends DummyCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {

		@Override
		public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {

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
	}

	public static class DummyMoistenerManager extends DummyCraftingProvider<IMoistenerRecipe> implements IMoistenerManager {

		@Override
		public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {

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

		@Override
		public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, @Nullable ItemStack remnants, float chance) {

		}
	}

	public static class DummyStillManager extends DummyCraftingProvider<IStillRecipe> implements IStillManager {

		@Override
		public void addRecipe(int cyclesPerUnit, FluidStack input, FluidStack output) {

		}
	}
}
