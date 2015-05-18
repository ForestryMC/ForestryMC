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
import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketUpdate;
import forestry.storage.GuiHandlerStorage.PagedBackpackInventory;

public class ContainerNaturalistBackpack extends ContainerItemInventory<PagedBackpackInventory> implements IGuiSelectable {

	public ContainerNaturalistBackpack(InventoryPlayer player, PagedBackpackInventory inventory, int page, int pageSize) {
		super(inventory);

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlotToContainer(new SlotFiltered(inventory, y + page * pageSize + x * 5, 100 + y * 18, 21 + x * 18));
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSecuredSlot(player, l1 + i1 * 9 + 9, 18 + l1 * 18, 120 + i1 * 18);
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSecuredSlot(player, j1, 18 + j1 * 18, 178);
		}
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {
		inventory.flipPage(player, packet.payload.intPayload[0]);
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}
}
