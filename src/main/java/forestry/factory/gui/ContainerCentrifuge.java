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
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.factory.tiles.TileCentrifuge;

public class ContainerCentrifuge extends ContainerSocketed<TileCentrifuge> {

	public ContainerCentrifuge(InventoryPlayer player, TileCentrifuge tile) {
		super(tile, player, 8, 84);

		// Resource
		this.addSlotToContainer(new SlotFiltered(tile, 0, 16, 37));

		// Craft Preview display
		addSlotToContainer(new SlotLocked(tile.getCraftPreviewInventory(), 0, 49, 37));

		// Product Inventory
		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 3; k++) {
				addSlotToContainer(new SlotOutput(tile, 1 + k + l * 3, 112 + k * 18, 19 + l * 18));
			}
		}
	}

	private ItemStack oldCraftPreview = ItemStack.EMPTY;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		IInventory craftPreviewInventory = tile.getCraftPreviewInventory();

		ItemStack newCraftPreview = craftPreviewInventory.getStackInSlot(0);
		if (!ItemStack.areItemStacksEqual(oldCraftPreview, newCraftPreview)) {
			oldCraftPreview = newCraftPreview;

			PacketItemStackDisplay packet = new PacketItemStackDisplay(tile, newCraftPreview);
			sendPacketToListeners(packet);
		}
	}

}
