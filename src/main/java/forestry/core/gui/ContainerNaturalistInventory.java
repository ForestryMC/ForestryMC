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
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketUpdate;

public class ContainerNaturalistInventory extends ContainerTile<TileNaturalistChest> implements IGuiSelectable {

	public ContainerNaturalistInventory(InventoryPlayer player, TileNaturalistChest tile, int page, int pageSize) {
		super(tile, player, 18, 120);

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlotToContainer(new SlotFiltered(tile, y + page * pageSize + x * 5, 100 + y * 18, 21 + x * 18));
			}
		}
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {
		tile.flipPage(player, packet.payload.intPayload[0]);
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}
}
