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

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketGuiSelect;
import forestry.storage.GuiHandlerStorage.PagedBackpackInventory;

public class ContainerNaturalistBackpack extends ContainerItemInventory<PagedBackpackInventory> implements IGuiSelectable {

	public ContainerNaturalistBackpack(EntityPlayer player, PagedBackpackInventory inventory, int page, int pageSize) {
		super(inventory, player.inventory, 18, 120);

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlotToContainer(new SlotFiltered(inventory, y + page * pageSize + x * 5, 100 + y * 18, 21 + x * 18));
			}
		}
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketGuiSelect packet) {
		inventory.flipPage(player, packet.getPrimaryIndex());
	}

	@Override
	public void setSelection(PacketGuiSelect packet) {
	}
}
