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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import forestry.api.core.INBTTagable;
import forestry.core.gui.ContainerDummy;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.InventoryPlain;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;

public class RecipeMemory implements INBTTagable, IStreamable {

	public static final int capacity = 9;

	private static final Container DUMMY_CONTAINER = new ContainerDummy();
	private static final List<Class<? extends Item>> memoryBlacklist = new ArrayList<>();

	static {
		memoryBlacklist.add(ItemMap.class); // almost every ItemMap is unique
	}

	private LinkedList<Recipe> recipes = new LinkedList<>();
	private long lastUpdate;

	private static boolean isValid(World world, Recipe recipe) {
		if (recipe == null) {
			return false;
		}
		ItemStack recipeOutput = recipe.getRecipeOutput(world);
		if (recipeOutput == null) {
			return false;
		}
		Item item = recipeOutput.getItem();
		return item != null && !memoryBlacklist.contains(item.getClass());
	}

	public void validate(World world) {
		LinkedList<Recipe> validRecipes = new LinkedList<>();
		for (Recipe recipe : recipes) {
			if (isValid(world, recipe)) {
				validRecipes.add(recipe);
			}
		}
		this.recipes = validRecipes;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void memorizeRecipe(World world, Recipe recipe, InventoryCrafting crafting) {

		if (!isValid(world, recipe)) {
			return;
		}

		lastUpdate = world.getTotalWorldTime();
		recipe.updateLastUse(lastUpdate);

		Recipe memory = getMemorized(crafting, world);
		if (memory != null) {
			if (memory.isLocked() != recipe.isLocked()) {
				recipe.toggleLock();
			}
			int index = recipes.indexOf(memory);
			recipes.set(index, recipe);
			return;
		}

		if (recipes.size() < capacity) {
			recipes.add(recipe);
			return;
		}

		Recipe oldest = null;
		for (Recipe existing : recipes) {
			if (oldest != null && oldest.getLastUsed() < existing.getLastUsed()) {
				continue;
			}
			if (existing.isLocked()) {
				continue;
			}

			oldest = existing;
		}
		if (oldest != null) {
			recipes.remove(oldest);
			recipes.add(recipe);
		}

	}

	private Recipe getRecipe(int recipeIndex) {
		if (recipes.size() > recipeIndex) {
			return recipes.get(recipeIndex);
		}

		return null;
	}

	public IInventory getRecipeMatrix(int recipeIndex) {
		RecipeMemory.Recipe recipe = getRecipe(recipeIndex);
		recipe.sanitizeMatrix();
		return recipe.getMatrix();
	}

	public ItemStack getRecipeOutput(World world, int recipeIndex) {
		if (recipes.size() > recipeIndex) {
			return recipes.get(recipeIndex).getRecipeOutput(world);
		} else {
			return null;
		}
	}

	public boolean isLocked(int recipeIndex) {
		if (recipes.size() > recipeIndex) {
			return recipes.get(recipeIndex).isLocked();
		}

		return false;
	}

	public void toggleLock(World world, int recipeIndex) {
		lastUpdate = world.getTotalWorldTime();
		if (recipes.size() > recipeIndex) {
			recipes.get(recipeIndex).toggleLock();
		}
	}

	private Recipe getMemorized(InventoryCrafting crafting, World world) {
		for (Recipe recipe : recipes) {
			if (recipe.hasSameOutput(crafting, world)) {
				return recipe;
			}
		}

		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		recipes = new LinkedList<>();
		if (!nbttagcompound.hasKey("RecipeMemory")) {
			return;
		}

		NBTTagList nbttaglist = nbttagcompound.getTagList("RecipeMemory", 10);
		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
			recipes.add(new Recipe(nbttagcompound2));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (Recipe recipe : recipes) {
			if (recipe != null) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				recipe.writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}
		}
		nbttagcompound.setTag("RecipeMemory", nbttaglist);
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeStreamables(recipes);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		data.readStreamables(recipes, Recipe.class);
	}

	public static final class Recipe implements INBTTagable, IStreamable {

		private InventoryAdapter matrix;
		private long lastUsed;
		private boolean locked;
		private ItemStack cachedRecipeOutput;

		@SuppressWarnings("unused")
		public Recipe() {
			// required for IStreamable serialization
		}

		public Recipe(InventoryCrafting crafting) {
			this.matrix = new InventoryAdapter(new InventoryPlain(crafting));
		}

		public Recipe(NBTTagCompound nbttagcompound) {
			readFromNBT(nbttagcompound);
		}

		public void updateLastUse(long lastUsed) {
			this.lastUsed = lastUsed;
		}

		public void toggleLock() {
			locked = !locked;
		}

		public InventoryAdapter getMatrix() {
			return matrix;
		}

		public ItemStack getRecipeOutput(World world) {
			if (cachedRecipeOutput == null) {
				InventoryCrafting crafting = new InventoryCrafting(DUMMY_CONTAINER, 3, 3);
				for (int i = 0; i < crafting.getSizeInventory(); i++) {
					crafting.setInventorySlotContents(i, matrix.getStackInSlot(i));
				}

				cachedRecipeOutput = CraftingManager.getInstance().findMatchingRecipe(crafting, world);
			}
			return cachedRecipeOutput;
		}

		public long getLastUsed() {
			return this.lastUsed;
		}

		public boolean isLocked() {
			return this.locked;
		}

		public boolean hasSameOutput(InventoryCrafting crafting, World world) {
			ItemStack recipeOutput = getRecipeOutput(world);
			if (recipeOutput == null) {
				return false;
			}

			ItemStack matchingRecipeOutput = CraftingManager.getInstance().findMatchingRecipe(crafting, world);
			return ItemStack.areItemStacksEqual(recipeOutput, matchingRecipeOutput);
		}

		@Override
		public final void readFromNBT(NBTTagCompound nbttagcompound) {
			matrix = new InventoryAdapter(new InventoryCrafting(DUMMY_CONTAINER, 3, 3));
			matrix.readFromNBT(nbttagcompound);
			sanitizeMatrix();
			lastUsed = nbttagcompound.getLong("LastUsed");
			locked = nbttagcompound.getBoolean("Locked");
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			sanitizeMatrix();
			matrix.writeToNBT(nbttagcompound);
			nbttagcompound.setLong("LastUsed", lastUsed);
			nbttagcompound.setBoolean("Locked", locked);
		}

		@Override
		public void writeData(DataOutputStreamForestry data) throws IOException {
			sanitizeMatrix();
			matrix.writeData(data);
			data.writeLong(lastUsed);
			data.writeBoolean(locked);
		}

		@Override
		public void readData(DataInputStreamForestry data) throws IOException {
			matrix = new InventoryAdapter(new InventoryCrafting(DUMMY_CONTAINER, 3, 3));
			matrix.readData(data);
			sanitizeMatrix();
			lastUsed = data.readLong();
			locked = data.readBoolean();
		}

		/**
		 * This is a nasty hack to hide a bug where the recipe stacks somehow
		 * get the stackSize set to -1. Which makes the recipe no longer work,
		 * ever. I've seen the bug, even inspected the NBT data of an afflicted
		 * worktable, but can't duplicate it, and can't trace it. -CovertJaguar
		 */
		private void sanitizeMatrix() {
			for (int slot = 0; slot < matrix.getSizeInventory(); slot++) {
				ItemStack stack = matrix.getStackInSlot(slot);
				if (stack != null) {
					stack.stackSize = 1;
				}
			}
		}
	}

}
