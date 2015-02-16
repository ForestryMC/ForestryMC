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

public class ContainerCarpenter extends ContainerLiquidTanks implements IContainerCrafting {

	private MachineCarpenter machine;
	private final IInventory craftingInventory;
	public final InventoryCraftingAuto craftMatrix;
	public final InventoryCraftResult craftResult;

	public ContainerCarpenter(InventoryPlayer inventoryplayer, MachineCarpenter tile) {
		super(tile);

		machine = tile;
		machine.activeContainer = this;
		craftingInventory = machine.getCraftingInventory();

		craftMatrix = new InventoryCraftingAuto(this, 3, 3);
		craftResult = new InventoryCraftResult();

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(machine, MachineCarpenter.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Liquid Input
		this.addSlotToContainer(new SlotFiltered(machine, MachineCarpenter.SLOT_CAN_INPUT, 120, 20));
		// Boxes
		this.addSlotToContainer(new SlotFiltered(machine, MachineCarpenter.SLOT_BOX, 83, 20));
		// Product
		this.addSlotToContainer(new SlotOutput(machine, MachineCarpenter.SLOT_PRODUCT, 120, 56));

		// CraftResult display
		addSlotToContainer(new SlotLocked(craftResult, 0, 80, 51));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlotToContainer(new SlotCraftMatrix(this, craftingInventory, k1 + l * 3, 10 + k1 * 18, 20 + l * 18));
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlotToContainer(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 136 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(inventoryplayer, j1, 8 + j1 * 18, 194));
		}

		// Update crafting matrix with current contents of tileentity.
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, craftingInventory.getStackInSlot(i));
		}
	}

	public ContainerCarpenter(MachineCarpenter tile) {
		super(tile);
		craftMatrix = new InventoryCraftingAuto(this, 3, 3);
		craftResult = new InventoryCraftResult();
		craftingInventory = tile.getCraftingInventory();

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
		if (machine != null) {
			machine.resetProductDisplay(craftMatrix);
			updateProductSlot();
		}
	}

	private void updateProductSlot() {
		// Update crafting display
		if (machine.currentRecipe != null) {
			craftResult.setInventorySlotContents(0, machine.currentRecipe.getCraftingResult());
		} else {
			craftResult.setInventorySlotContents(0, null);
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		machine.activeContainer = null;
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
