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
package forestry.apiculture.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.apiculture.network.packets.PacketImprintSelectionResponse;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;

public class ContainerImprinter extends ContainerItemInventory<ItemInventoryImprinter> implements IGuiSelectable {

	public ContainerImprinter(InventoryPlayer inventoryplayer, ItemInventoryImprinter inventory) {
		super(inventory, inventoryplayer, 8, 103);

		// Input
		this.addSlotToContainer(new SlotFiltered(inventory, 0, 152, 12));
		// Output
		this.addSlotToContainer(new SlotOutput(inventory, 1, 152, 72));
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, PacketGuiSelectRequest packetRequest) {
		if (packetRequest.getPrimaryIndex() == 0) {
			if (packetRequest.getSecondaryIndex() == 0) {
				inventory.advancePrimary();
			} else {
				inventory.regressPrimary();
			}
		} else {
			if (packetRequest.getSecondaryIndex() == 0) {
				inventory.advanceSecondary();
			} else {
				inventory.regressSecondary();
			}
		}

		PacketImprintSelectionResponse packetResponse = new PacketImprintSelectionResponse(inventory.getPrimaryIndex(), inventory.getSecondaryIndex());
		Proxies.net.sendToPlayer(packetResponse, player);
	}

	public void setSelection(PacketImprintSelectionResponse packetPayload) {
		inventory.setPrimaryIndex(packetPayload.getPrimaryIndex());
		inventory.setSecondaryIndex(packetPayload.getSecondaryIndex());
	}
}
