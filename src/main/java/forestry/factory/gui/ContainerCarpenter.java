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

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.factory.inventory.InventoryCarpenter;
import forestry.factory.tiles.TileCarpenter;

public class ContainerCarpenter extends ContainerLiquidTanks<TileCarpenter> implements IContainerCrafting {

	public ContainerCarpenter(InventoryPlayer inventoryplayer, TileCarpenter tile) {
		super(tile, inventoryplayer, 8, 136);

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(tile, InventoryCarpenter.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 90 + i * 18));
			}
		}

		// Liquid Input
		this.addSlotToContainer(new SlotFiltered(tile, InventoryCarpenter.SLOT_CAN_INPUT, 120, 20));
		// Boxes
		this.addSlotToContainer(new SlotFiltered(tile, InventoryCarpenter.SLOT_BOX, 83, 20));
		// Product
		this.addSlotToContainer(new SlotOutput(tile, InventoryCarpenter.SLOT_PRODUCT, 120, 56));

		// Craft Preview display
		addSlotToContainer(new SlotLocked(tile.getCraftPreviewInventory(), 0, 80, 51));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlotToContainer(new SlotCraftMatrix(this, tile.getCraftingInventory(), k1 + l * 3, 10 + k1 * 18, 20 + l * 18));
			}
		}
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		tile.checkRecipe();
	}

	private ItemStack oldCraftPreview;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		IInventory craftPreviewInventory = tile.getCraftPreviewInventory();

		ItemStack newCraftPreview = craftPreviewInventory.getStackInSlot(0);
		if (!ItemStack.areItemStacksEqual(oldCraftPreview, newCraftPreview)) {
			oldCraftPreview = newCraftPreview;

			PacketItemStackDisplay packet = new PacketItemStackDisplay(tile, newCraftPreview);
			sendPacketToCrafters(packet);
		}
	}

}
