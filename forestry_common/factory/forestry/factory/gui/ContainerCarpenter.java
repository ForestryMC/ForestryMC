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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotCraftAuto;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.interfaces.IContainerCrafting;
import forestry.core.proxy.Proxies;
import forestry.factory.gadgets.MachineCarpenter;

public class ContainerCarpenter extends ContainerLiquidTanks implements IContainerCrafting {

	private class SlotCrate extends SlotCraftAuto {

		public SlotCrate(IContainerCrafting container, IInventory iinventory, int slotNumber, int x, int y) {
			super(container, iinventory, slotNumber, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return MachineCarpenter.RecipeManager.isBox(stack);
		}
	}
	private MachineCarpenter machine;
	private final IInventory craftingInventory;
	private IInventory internalInventory;
	public InventoryCraftingAuto craftMatrix;
	public InventoryCraftResult craftResult;

	public ContainerCarpenter(InventoryPlayer inventoryplayer, MachineCarpenter tile) {
		super(tile.getInternalInventory(), tile);

		machine = tile;
		machine.activeContainer = this;
		craftingInventory = machine.getCraftingInventory();
		internalInventory = machine.getInternalInventory();

		craftMatrix = new InventoryCraftingAuto(this, 3, 3);
		craftResult = new InventoryCraftResult();

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlot(new Slot(internalInventory, MachineCarpenter.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Liquid Input
		this.addSlot(new SlotLiquidContainer(internalInventory, MachineCarpenter.SLOT_CAN_INPUT, 120, 20));
		// Boxes
		this.addSlot(new SlotCrate(this, internalInventory, MachineCarpenter.SLOT_BOX, 83, 20));
		// Product
		this.addSlot(new SlotOutput(internalInventory, MachineCarpenter.SLOT_PRODUCT, 120, 56));

		// CraftResult display
		addSlot(new SlotLocked(craftResult, 0, 80, 51));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlot(new SlotCraftMatrix(this, craftingInventory, k1 + l * 3, 10 + k1 * 18, 20 + l * 18));
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 136 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 194));
		}

		// Update crafting matrix with current contents of tileentity.
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, craftingInventory.getStackInSlot(i));
		}
	}

	public ContainerCarpenter(MachineCarpenter tile) {
		super(tile.getInternalInventory(), tile);
		craftMatrix = new InventoryCraftingAuto(this, 3, 3);
		craftResult = new InventoryCraftResult();
		craftingInventory = tile.getCraftingInventory();

		// Update crafting matrix with current contents of tileentity.
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			craftMatrix.setInventorySlotContents(i, craftingInventory.getStackInSlot(i));
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		machine.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			machine.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}

		updateProductSlot();
	}

	/**
	 * Replaces the original onCraftMatrixChanged
	 *
	 * @param iinventory
	 * @param slot
	 */
	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		craftingInventory.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
		if (slot < craftMatrix.stackList.length)
			craftMatrix.stackList[slot] = iinventory.getStackInSlot(slot);
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
			machine.setCurrentRecipe(MachineCarpenter.RecipeManager.findMatchingRecipe(machine.resourceTank.getFluid(), machine.getBoxStack(), craftMatrix,
					machine.getWorldObj()));
			updateProductSlot();
		}
	}

	private void updateProductSlot() {
		// Update crafting display
		if (machine.currentRecipe != null)
			craftResult.setInventorySlotContents(0, machine.currentRecipe.getCraftingResult());
		else
			craftResult.setInventorySlotContents(0, null);
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		machine.activeContainer = null;
		if (entityplayer == null)
			return;

		InventoryPlayer inventoryplayer = entityplayer.inventory;
		if (inventoryplayer.getItemStack() != null) {
			Proxies.common.dropItemPlayer(entityplayer, inventoryplayer.getItemStack());
			inventoryplayer.setItemStack(null);
		}

	}
}
