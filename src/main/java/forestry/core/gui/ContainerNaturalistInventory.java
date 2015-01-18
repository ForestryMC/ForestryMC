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
import net.minecraft.inventory.Slot;

import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketUpdate;

public class ContainerNaturalistInventory extends ContainerForestry implements IGuiSelectable {

	private final IPagedInventory inv;

	public ContainerNaturalistInventory(InventoryPlayer player, TileNaturalistChest tile, int page, int pageSize) {
		super(tile);
		this.inv = tile;

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlotToContainer(new SlotFiltered(tile, y + page * pageSize + x * 5, 100 + y * 18, 21 + x * 18));
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlotToContainer(new Slot(player, l1 + i1 * 9 + 9, 18 + l1 * 18, 120 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(player, j1, 18 + j1 * 18, 178));
		}
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {
		inv.flipPage(player, packet.payload.intPayload[0]);
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}
}
