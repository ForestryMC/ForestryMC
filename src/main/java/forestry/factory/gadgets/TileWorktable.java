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

import forestry.api.core.ForestryAPI;
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
import forestry.core.utils.StackUtils;
import forestry.core.utils.TileInventoryAdapter;
import forestry.factory.recipes.RecipeMemory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

public class TileWorktable extends TileBase implements ICrafter {

	/* CONSTANTS */
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_CRAFTING_COUNT = 9;
	public final static int SLOT_CRAFTING_RESULT = 9;
	public final static short SLOT_INVENTORY_1 = 0;
	public final static short SLOT_INVENTORY_COUNT = 18;
	private final static Container DUMMY_CONTAINER = new ContainerDummy();

	/* MEMBERS */
	private RecipeMemory.Recipe currentRecipe;
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

		IInventory matrix = memorized.getRecipeMatrix(recipeIndex);
		if (matrix == null)
			return;

		for (int slot = 0; slot < matrix.getSizeInventory(); slot++) {
			craftingInventory.setInventorySlotContents(slot, matrix.getStackInSlot(slot));
		}
	}

	/* CRAFTING */
	public void setRecipe(InventoryCrafting crafting) {

		ItemStack recipeOutput = CraftingManager.getInstance().findMatchingRecipe(crafting, worldObj);
		if (recipeOutput == null) {
			currentRecipe = null;
			currentCrafting = null;
		} else {
			currentRecipe = new RecipeMemory.Recipe(crafting, worldObj);
			currentCrafting = crafting;
		}
		updateCraftResult();
	}

	private void updateCraftResult() {
		if (currentRecipe != null) {
			ItemStack result = currentRecipe.getRecipeOutput(worldObj);
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
		ItemStack[] set = craftingInventory.getStacks(SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
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
		ItemStack recipeOutput = CraftingManager.getInstance().findMatchingRecipe(crafting, worldObj);
		if (recipeOutput == null)
			return false;

		return ItemStack.areItemStacksEqual(recipeOutput, currentRecipe.getRecipeOutput(worldObj));
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

		updateCraftResult();
		return currentRecipe.getRecipeOutput(worldObj).copy();
	}

	@Override
	public ItemStack getResult() {
		if (currentRecipe == null)
			return null;

		if (currentRecipe.getRecipeOutput(worldObj) != null)
			return currentRecipe.getRecipeOutput(worldObj).copy();
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
