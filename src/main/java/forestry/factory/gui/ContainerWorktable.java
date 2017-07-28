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
package forestry.factory.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotCrafter;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.inventory.InventoryCraftingForestry;
import forestry.factory.inventory.InventoryGhostCrafting;
import forestry.factory.inventory.InventoryWorktable;
import forestry.factory.network.packets.PacketWorktableMemoryUpdate;
import forestry.factory.network.packets.PacketWorktableRecipeRequest;
import forestry.factory.network.packets.PacketWorktableRecipeUpdate;
import forestry.factory.recipes.MemorizedRecipe;
import forestry.factory.recipes.RecipeMemory;
import forestry.factory.tiles.TileWorktable;

public class ContainerWorktable extends ContainerTile<TileWorktable> implements IContainerCrafting, IGuiSelectable {
	private final InventoryCraftingForestry craftMatrix = new InventoryCraftingForestry(this);
	private long lastMemoryUpdate;
	private boolean craftMatrixChanged = false;

	public ContainerWorktable(EntityPlayer player, TileWorktable tile) {
		super(tile, player.inventory, 8, 136);

		IInventory craftingDisplay = tile.getCraftingDisplay();
		IInventory internalInventory = tile.getInternalInventory();

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(internalInventory, InventoryWorktable.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlotToContainer(new SlotCraftMatrix(this, craftingDisplay, k1 + l * 3, 11 + k1 * 18, 20 + l * 18));
			}
		}

		// CraftResult display
		addSlotToContainer(new SlotCrafter(player, craftMatrix, craftingDisplay, tile, InventoryGhostCrafting.SLOT_CRAFTING_RESULT, 77, 38));

		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			onCraftMatrixChanged(tile.getCraftingDisplay(), i);
		}
	}

	@Override
	public void detectAndSendChanges() {
		if (craftMatrixChanged) {
			craftMatrixChanged = false;
			tile.setCurrentRecipe(craftMatrix);
			sendPacketToListeners(new PacketWorktableRecipeUpdate(tile));
		}

		super.detectAndSendChanges();

		if (lastMemoryUpdate != tile.getMemory().getLastUpdate()) {
			lastMemoryUpdate = tile.getMemory().getLastUpdate();
			sendPacketToListeners(new PacketWorktableMemoryUpdate(tile));
		}
	}

	public void updateCraftMatrix() {
		IInventory crafting = tile.getCraftingDisplay();
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			onCraftMatrixChanged(crafting, i);
		}
	}

	// Fired when SlotCraftMatrix detects a change.
	// Direct changes to the underlying inventory are not detected, only slot changes.
	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		if (slot >= craftMatrix.getSizeInventory()) {
			return;
		}

		ItemStack stack = iinventory.getStackInSlot(slot);
		ItemStack currentStack = craftMatrix.getStackInSlot(slot);

		if (!ItemStackUtil.isIdenticalItem(stack, currentStack)) {
			craftMatrix.setInventorySlotContents(slot, stack);
		}
	}

	// Fired when this container's craftMatrix detects a change
	@Override
	public void onCraftMatrixChanged(IInventory iinventory) {
		craftMatrixChanged = true;
	}

	/* Gui Selection Handling */
	public static void clearRecipe() {
		sendRecipeClick(-1, 0);
	}

	public static void sendRecipeClick(int mouseButton, int recipeIndex) {
		Proxies.net.sendToServer(new PacketGuiSelectRequest(mouseButton, recipeIndex));
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, PacketGuiSelectRequest packet) {
		int primary = packet.getPrimaryIndex();
		int secondary = packet.getSecondaryIndex();

		switch (primary) {
			case -1: { // clicked clear button
				tile.clearCraftMatrix();
				updateCraftMatrix();
				sendPacketToListeners(new PacketWorktableRecipeUpdate(tile));
				break;
			}
			case 0: { // clicked a memorized recipe
				tile.chooseRecipeMemory(secondary);
				updateCraftMatrix();
				sendPacketToListeners(new PacketWorktableRecipeUpdate(tile));
				break;
			}
			case 1: { // right clicked a memorized recipe
				long time = player.worldObj.getTotalWorldTime();
				RecipeMemory memory = tile.getMemory();
				memory.toggleLock(time, secondary);
				break;
			}
			case 100: { // clicked previous recipe conflict button
				tile.choosePreviousConflictRecipe();
				sendPacketToListeners(new PacketWorktableRecipeUpdate(tile));
				break;
			}
			case 101: { // clicked next recipe conflict button
				tile.chooseNextConflictRecipe();
				sendPacketToListeners(new PacketWorktableRecipeUpdate(tile));
				break;
			}
		}
	}

	public void sendWorktableRecipeRequest(MemorizedRecipe recipe) {
		Proxies.net.sendToServer(new PacketWorktableRecipeRequest(tile, recipe));
	}
}
