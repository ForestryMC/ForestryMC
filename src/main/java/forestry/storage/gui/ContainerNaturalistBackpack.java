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
package forestry.storage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketGuiSelectRequest;
import forestry.storage.inventory.ItemInventoryBackpackPaged;

public class ContainerNaturalistBackpack extends ContainerItemInventory<ItemInventoryBackpackPaged> implements IGuiSelectable {

	public ContainerNaturalistBackpack(EntityPlayer player, ItemInventoryBackpackPaged inventory, int page) {
		super(inventory, player.inventory, 18, 120);

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlotToContainer(new SlotFiltered(inventory, y + page * 25 + x * 5, 100 + y * 18, 21 + x * 18));
			}
		}
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, PacketGuiSelectRequest packet) {
		inventory.flipPage(player, packet.getPrimaryIndex());
	}
}
