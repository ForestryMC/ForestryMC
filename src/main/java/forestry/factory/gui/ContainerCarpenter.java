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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.interfaces.IContainerCrafting;
import forestry.core.proxy.Proxies;
import forestry.factory.gadgets.MachineCarpenter;

public class ContainerCarpenter extends ContainerLiquidTanks<MachineCarpenter> implements IContainerCrafting {

	private final IInventory craftingInventory;
	public final InventoryCraftingAuto craftMatrix;
	public final InventoryCraftResult craftResult;

	public ContainerCarpenter(InventoryPlayer inventoryplayer, MachineCarpenter tile) {
		super(tile, inventoryplayer, 8, 136);

		this.tile.activeContainer = this;
		craftingInventory = this.tile.getCraftingInventory();

		craftMatrix = new InventoryCraftingAuto(this, 3, 3);
		craftResult = new InventoryCraftResult();

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(this.tile, MachineCarpenter.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Liquid Input
		this.addSlotToContainer(new SlotFiltered(this.tile, MachineCarpenter.SLOT_CAN_INPUT, 120, 20));
		// Boxes
		this.addSlotToContainer(new SlotFiltered(this.tile, MachineCarpenter.SLOT_BOX, 83, 20));
		// Product
		this.addSlotToContainer(new SlotOutput(this.tile, MachineCarpenter.SLOT_PRODUCT, 120, 56));

		// CraftResult display
		addSlotToContainer(new SlotLocked(craftResult, 0, 80, 51));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlotToContainer(new SlotCraftMatrix(this, craftingInventory, k1 + l * 3, 10 + k1 * 18, 20 + l * 18));
			}
		}

		// Update crafting matrix with current contents of tileentity.
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, craftingInventory.getStackInSlot(i));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		updateProductSlot();
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		craftingInventory.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
		if (slot < craftMatrix.stackList.length) {
			craftMatrix.stackList[slot] = iinventory.getStackInSlot(slot);
		}
		resetProductDisplay();
	}

	public void updateProductDisplay() {
		// Update crafting matrix with current contents of tileentity.
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, craftingInventory.getStackInSlot(i));
		}
		resetProductDisplay();
	}

	private void resetProductDisplay() {
		if (tile != null) {
			tile.resetProductDisplay(craftMatrix);
			updateProductSlot();
		}
	}

	private void updateProductSlot() {
		// Update crafting display
		if (tile.currentRecipe != null) {
			craftResult.setInventorySlotContents(0, tile.currentRecipe.getCraftingResult());
		} else {
			craftResult.setInventorySlotContents(0, null);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		tile.activeContainer = null;
		if (entityplayer == null) {
			return;
		}

		InventoryPlayer inventoryplayer = entityplayer.inventory;
		if (inventoryplayer.getItemStack() != null) {
			Proxies.common.dropItemPlayer(entityplayer, inventoryplayer.getItemStack());
			inventoryplayer.setItemStack(null);
		}

	}
}
