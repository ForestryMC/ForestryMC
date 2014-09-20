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
package forestry.factory.gadgets;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.core.INBTTagable;
import forestry.core.gadgets.TileBase;
import forestry.core.gui.ContainerDummy;
import forestry.core.interfaces.ICrafter;
import forestry.core.network.ForestryPacket;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.PlainInventory;
import forestry.core.utils.StackUtils;
import forestry.core.utils.TileInventoryAdapter;

public class TileWorktable extends TileBase implements ICrafter {

	/* CONSTANTS */
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_CRAFTING_RESULT = 9;
	public final static short SLOT_INVENTORY_1 = 0;
	public final static short SLOT_INVENTORY_COUNT = 18;
	private final static Container DUMMY_CONTAINER = new ContainerDummy();
	private final static RecipeBridge RECIPE_BRIDGE = new RecipeBridge();

	private static class RecipeBridge {
		public ItemStack findMatchingRecipe(InventoryCrafting crafting, World world) {
			return CraftingManager.getInstance().findMatchingRecipe(crafting, world);
		}
	}

	/* RECIPE MEMORY */
	public static final class Recipe implements INBTTagable {

		private InventoryAdapter matrix;
		private long lastUsed;
		private boolean locked;
		private World cachedWorld;
		private ItemStack cachedRecipeOutput;

		public Recipe(InventoryCrafting crafting) {
			this.matrix = new InventoryAdapter(new PlainInventory(crafting));
		}

		public Recipe(NBTTagCompound nbttagcompound) {
			readFromNBT(nbttagcompound);
		}

		public void setCacheWorld(World world) {
			this.cachedWorld = world;
		}

		private boolean updateCachedRecipeOutput() {

			if (cachedRecipeOutput != null)
				return true;

			InventoryCrafting crafting = new InventoryCrafting(DUMMY_CONTAINER, 3, 3);
			for (int i = 0; i < crafting.getSizeInventory(); i++) {
				crafting.setInventorySlotContents(i, matrix.getStackInSlot(i));
			}

			cachedRecipeOutput = RECIPE_BRIDGE.findMatchingRecipe(crafting, cachedWorld);
			return cachedRecipeOutput != null;
		}

		public void updateLastUse(World world) {
			lastUsed = world.getTotalWorldTime();
			setCacheWorld(world);
		}

		public void toogleLock() {
			locked = !locked;
		}

		public InventoryAdapter getMatrix() {
			return matrix;
		}

		public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
			if (updateCachedRecipeOutput())
				return cachedRecipeOutput;

			return null;
		}

		public ItemStack getRecipeOutput() {
			if (updateCachedRecipeOutput())
				return cachedRecipeOutput;

			return null;
		}

		public long getLastUsed() {
			return this.lastUsed;
		}

		public boolean isLocked() {
			return this.locked;
		}

		public int getRecipeSize() {
			return matrix.getSizeInventory();
		}

		public boolean hasSameOutput(InventoryCrafting crafting, World world) {

			if (!updateCachedRecipeOutput())
				return false;

			ItemStack recipeOutput = RECIPE_BRIDGE.findMatchingRecipe(crafting, world);
			return recipeOutput != null && cachedRecipeOutput.isItemEqual(recipeOutput);
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

		/**
		 * This is a nasty hack to hide a bug where the recipe stacks somehow
		 * get the stackSize set to -1. Which makes the recipe no longer work,
		 * ever. I've seen the bug, even inspected the NBT data of an afflicted
		 * worktable, but can't duplicate it, and can't trace it. -CovertJaguar
		 */
		private void sanitizeMatrix() {
			for (int slot = 0; slot < matrix.getSizeInventory(); slot++) {
				ItemStack stack = matrix.getStackInSlot(slot);
				if (stack != null)
					stack.stackSize = 1;
			}
		}
	}

	public static class RecipeMemory implements INBTTagable, IInventory {

		private LinkedList<Recipe> recipes = new LinkedList<Recipe>();
		private long lastUpdate;
		public final int capacity = 9;

		public long getLastUpdate() {
			return lastUpdate;
		}

		public void memorizeRecipe(World world, Recipe recipe, InventoryCrafting crafting) {

			lastUpdate = world.getTotalWorldTime();
			recipe.updateLastUse(world);

			Recipe memory = getMemorized(crafting, world);
			if (memory != null) {
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
				if (oldest != null && oldest.getLastUsed() < existing.getLastUsed())
					continue;
				if (existing.isLocked())
					continue;

				oldest = existing;
			}
			if (oldest != null) {
				recipes.remove(oldest);
				recipes.add(recipe);
			}

		}

		public Recipe getRecipe(int recipeIndex) {
			if (recipes.size() > recipeIndex)
				return recipes.get(recipeIndex);

			return null;
		}

		public ItemStack getRecipeOutput(int recipeIndex) {
			if (recipes.size() > recipeIndex)
				return recipes.get(recipeIndex).getRecipeOutput();
			else
				return null;
		}

		public boolean isLocked(int recipeIndex) {
			if (recipes.size() > recipeIndex)
				return recipes.get(recipeIndex).isLocked();

			return false;
		}

		public void toggleLock(World world, int recipeIndex) {
			lastUpdate = world.getTotalWorldTime();
			if (recipes.size() > recipeIndex)
				recipes.get(recipeIndex).toogleLock();
		}

		private Recipe getMemorized(InventoryCrafting crafting, World world) {
			for (Recipe recipe : recipes) {
				if (recipe.hasSameOutput(crafting, world))
					return recipe;
			}

			return null;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {
			recipes = new LinkedList<Recipe>();
			if (!nbttagcompound.hasKey("RecipeMemory"))
				return;

			NBTTagList nbttaglist = nbttagcompound.getTagList("RecipeMemory", 10);
			for (int j = 0; j < nbttaglist.tagCount(); ++j) {
				NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
				recipes.add(new Recipe(nbttagcompound2));
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < recipes.size(); i++) {
				if (recipes.get(i) != null) {
					NBTTagCompound nbttagcompound2 = new NBTTagCompound();
					recipes.get(i).writeToNBT(nbttagcompound2);
					nbttaglist.appendTag(nbttagcompound2);
				}
			}
			nbttagcompound.setTag("RecipeMemory", nbttaglist);
		}

		@Override
		public int getSizeInventory() {
			return recipes.size();
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return getRecipeOutput(i);
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			return null;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int i) {
			return null;
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
		}

		@Override
		public String getInventoryName() {
			return "";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void openInventory() {
		}

		@Override
		public void closeInventory() {
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
	}
	/* MEMBERS */
	private Recipe currentRecipe;
	private InventoryCrafting currentCrafting;
	private final RecipeMemory memorized;
	private final TileInventoryAdapter craftingInventory;
	private final TileInventoryAdapter accessibleInventory;

	public TileWorktable() {
		craftingInventory = new TileInventoryAdapter(this, 10, "CraftItems");
		accessibleInventory = new TileInventoryAdapter(this, 18, "Items");

		memorized = new RecipeMemory();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.WorktableGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		craftingInventory.writeToNBT(nbttagcompound);
		accessibleInventory.writeToNBT(nbttagcompound);

		memorized.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		craftingInventory.readFromNBT(nbttagcompound);
		accessibleInventory.readFromNBT(nbttagcompound);

		memorized.readFromNBT(nbttagcompound);
	}

	/* NETWORK */
	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		if (packetRaw instanceof PacketTileUpdate) {
			super.fromPacket(packetRaw);
			return;
		}

		PacketTileNBT packet = (PacketTileNBT) packetRaw;
		readFromNBT(packet.getTagCompound());
	}

	public void sendAll(EntityPlayer player) {
		Proxies.net.sendToPlayer(new PacketTileNBT(PacketIds.TILE_NBT, this), player);
	}

	/* RECIPE SELECTION */
	public RecipeMemory getMemory() {
		return memorized;
	}

	public void chooseRecipe(int recipeIndex) {
		if (recipeIndex >= memorized.capacity) {
			for (int slot = 0; slot < craftingInventory.getSizeInventory(); slot++) {
				craftingInventory.setInventorySlotContents(slot, null);
			}
			return;
		}

		Recipe recipe = memorized.getRecipe(recipeIndex);
		recipe.sanitizeMatrix();
		IInventory matrix = recipe.getMatrix();
		if (matrix == null)
			return;

		for (int slot = 0; slot < matrix.getSizeInventory(); slot++) {
			craftingInventory.setInventorySlotContents(slot, matrix.getStackInSlot(slot));
		}
	}

	/* CRAFTING */
	public void setRecipe(InventoryCrafting crafting) {

		ItemStack recipeOutput = RECIPE_BRIDGE.findMatchingRecipe(crafting, worldObj);
		if (recipeOutput == null) {
			currentRecipe = null;
			currentCrafting = null;
		} else {
			currentRecipe = new Recipe(crafting);
			currentCrafting = crafting;
		}
		updateCraftResult(currentCrafting);
	}

	private void updateCraftResult(InventoryCrafting inventorycrafting) {
		if (currentRecipe != null) {
			ItemStack result = currentRecipe.getCraftingResult(inventorycrafting);
			if (result != null) {
				craftingInventory.setInventorySlotContents(SLOT_CRAFTING_RESULT, result.copy());
				return;
			}
		}

		craftingInventory.setInventorySlotContents(SLOT_CRAFTING_RESULT, null);
	}

	private boolean canCraftCurrentRecipe() {
		if (currentRecipe == null)
			return false;

		// Need at least one matched set
		ItemStack[] set = craftingInventory.getStacks(SLOT_CRAFTING_1, 9);
		ItemStack[] stock = accessibleInventory.getStacks(SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		if (StackUtils.containsSets(set, stock, true, true) == 0)
			return false;

		// Check that it doesn't make a different recipe.
		// For example:
		// Wood Logs are all ore dictionary equivalent with each other,
		// but an Oak Log shouldn't be able to make Ebony Wood Planks
		// because it makes Oak Wood Planks using the same recipe.
		// Strategy:
		// Create a fake crafting inventory using items we have in stock
		// in place of the ones in the saved crafting inventory.
		// Check that the recipe it makes is the same as the currentRecipe.
		InventoryCrafting crafting = new InventoryCrafting(DUMMY_CONTAINER, 3, 3);
		ItemStack[] stockCopy = StackUtils.condenseStacks(stock);

		for (int slot = 0; slot < currentCrafting.getSizeInventory(); slot++) {
			ItemStack recipeStack = currentCrafting.getStackInSlot(slot);
			if (recipeStack == null)
				continue;

			// Use crafting equivalent (not oredict) items first
			for (ItemStack stockStack : stockCopy) {
				if (stockStack.stackSize > 0 && StackUtils.isCraftingEquivalent(recipeStack, stockStack, false, false)) {
					ItemStack stack = new ItemStack(stockStack.getItem(), 1, stockStack.getItemDamage());
					stockStack.stackSize--;
					crafting.setInventorySlotContents(slot, stack);
					break;
				}
			}

			// Use oredict items if crafting equivalent items aren't available
			if (crafting.getStackInSlot(slot) == null) {
				for (ItemStack stockStack : stockCopy) {
					if (stockStack.stackSize > 0 && StackUtils.isCraftingEquivalent(recipeStack, stockStack, true, true)) {
						ItemStack stack = new ItemStack(stockStack.getItem(), 1, stockStack.getItemDamage());
						stockStack.stackSize--;
						crafting.setInventorySlotContents(slot, stack);
						break;
					}
				}
			}
		}
		ItemStack recipeOutput = RECIPE_BRIDGE.findMatchingRecipe(crafting, worldObj);
		if (recipeOutput == null)
			return false;

		return recipeOutput.isItemEqual(currentRecipe.getRecipeOutput());
	}

	private boolean removeResources(EntityPlayer player) {
		ItemStack[] set = craftingInventory.getStacks(SLOT_CRAFTING_1, 9);
		return accessibleInventory.removeSets(1, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
	}

	@Override
	public boolean canTakeStack(int slotIndex) {
		if (slotIndex == SLOT_CRAFTING_RESULT)
			return canCraftCurrentRecipe();
		return true;
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		if (!removeResources(player))
			return null;

		if (Proxies.common.isSimulating(worldObj))
			memorized.memorizeRecipe(worldObj, currentRecipe, currentCrafting);

		updateCraftResult(currentCrafting);
		return currentRecipe.getRecipeOutput().copy();
	}

	@Override
	public ItemStack getResult() {
		if (currentRecipe == null)
			return null;

		if (currentRecipe.getRecipeOutput() != null)
			return currentRecipe.getRecipeOutput().copy();
		return null;
	}

	/* INVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return accessibleInventory;
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public InventoryAdapter getCraftingInventory() {
		return craftingInventory;
	}
}
