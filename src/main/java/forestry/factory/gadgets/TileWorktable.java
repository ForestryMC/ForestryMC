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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.ForestryAPI;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.ICrafter;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.ForestryPacket;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GuiUtil;
import forestry.core.utils.RecipeUtil;
import forestry.factory.recipes.RecipeMemory;

public class TileWorktable extends TileBase implements ICrafter {

	/* CONSTANTS */
	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_CRAFTING_COUNT = 9;
	public final static int SLOT_CRAFTING_RESULT = 9;
	public final static short SLOT_INVENTORY_1 = 0;
	public final static short SLOT_INVENTORY_COUNT = 18;

	/* MEMBERS */
	private RecipeMemory.Recipe currentRecipe;
	private InventoryCrafting currentCrafting;
	private final RecipeMemory memorized;
	private final TileInventoryAdapter craftingInventory;

	public TileWorktable() {
		craftingInventory = new TileInventoryAdapter(this, 10, "CraftItems");
		setInternalInventory(new TileInventoryAdapter(this, 18, "Items") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				return GuiUtil.isIndexInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
			}
		});

		memorized = new RecipeMemory();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.WorktableGUI.ordinal(), player.worldObj, pos.getX(), pos.getY(), pos.getZ());
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		craftingInventory.writeToNBT(nbttagcompound);

		memorized.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		craftingInventory.readFromNBT(nbttagcompound);

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

	@Override
	public void validate() {
		super.validate();
		memorized.validate(worldObj);
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
		if (matrix == null) {
			return;
		}

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
			currentRecipe = new RecipeMemory.Recipe(crafting);
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
		if (currentRecipe == null) {
			return false;
		}

		ItemStack[] recipeItems = InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		ItemStack[] inventory = InvTools.getStacks(getInternalInventory(), SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		ItemStack recipeOutput = currentRecipe.getRecipeOutput(worldObj);

		return RecipeUtil.canCraftRecipe(worldObj, recipeItems, recipeOutput, inventory);
	}

	private boolean removeResources(EntityPlayer player) {
		ItemStack[] set = InvTools.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		return InvTools.removeSets(getInternalInventory(), 1, set, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT, player, true, true, true);
	}

	@Override
	public boolean canTakeStack(int slotIndex) {
		if (slotIndex == SLOT_CRAFTING_RESULT) {
			return canCraftCurrentRecipe();
		}
		return true;
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		if (!removeResources(player)) {
			return null;
		}

		if (Proxies.common.isSimulating(worldObj)) {
			memorized.memorizeRecipe(worldObj, currentRecipe, currentCrafting);
		}

		updateCraftResult();
		return currentRecipe.getRecipeOutput(worldObj).copy();
	}

	@Override
	public ItemStack getResult() {
		if (currentRecipe == null) {
			return null;
		}

		if (currentRecipe.getRecipeOutput(worldObj) != null) {
			return currentRecipe.getRecipeOutput(worldObj).copy();
		}
		return null;
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public InventoryAdapter getCraftingInventory() {
		return craftingInventory;
	}
}
