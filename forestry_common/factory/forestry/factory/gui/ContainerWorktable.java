/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.factory.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerForestry;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotCrafter;
import forestry.core.interfaces.IContainerCrafting;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.factory.gadgets.TileWorktable;

public class ContainerWorktable extends ContainerForestry implements IContainerCrafting, IGuiSelectable {

	EntityPlayer player;
	TileWorktable tile;
	IInventory craftingInventory;
	IInventory internalInventory;
	InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	InventoryCraftResult craftResult = new InventoryCraftResult();
	long lastUpdate;

	public ContainerWorktable(EntityPlayer player, TileWorktable tile) {
		super(tile.getInternalInventory());
		this.tile = tile;

		craftingInventory = tile.getCraftingInventory();
		internalInventory = tile.getInternalInventory();

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlot(new Slot(internalInventory, TileWorktable.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlot(new SlotCraftMatrix(this, craftingInventory, k1 + l * 3, 11 + k1 * 18, 20 + l * 18));
			}
		}

		// CraftResult display
		addSlot(new SlotCrafter(craftingInventory, tile, TileWorktable.SLOT_CRAFTING_RESULT, 77, 38));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(player.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 136 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(player.inventory, j1, 8 + j1 * 18, 194));
		}

		// Update crafting matrix with current contents of tileentity.
		updateMatrix();
		updateRecipe();

		this.player = player;
		tile.sendAll(player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (lastUpdate == tile.getMemory().getLastUpdate())
			return;

		lastUpdate = tile.getMemory().getLastUpdate();
		tile.sendAll(player);
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
//		craftingInventory.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
		if (slot < craftMatrix.getSizeInventory())
			craftMatrix.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
		updateRecipe();
	}

	private void updateMatrix() {
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, craftingInventory.getStackInSlot(i));
		}
	}

	private void updateRecipe() {
		tile.setRecipe(craftMatrix);
	}

	public void sendRecipeClick(int mouseButton, int recipeIndex) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = mouseButton;
		payload.intPayload[1] = recipeIndex;
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {
		if (packet.payload.intPayload[0] > 0) {
			tile.getMemory().toggleLock(player.worldObj, packet.payload.intPayload[1]);
		} else {
			tile.chooseRecipe(packet.payload.intPayload[1]);
			updateMatrix();
			updateRecipe();
		}
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}
}
