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

import forestry.core.config.Defaults;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.inventory.ItemInventoryBackpack;

public class ContainerBackpack extends ContainerItemInventory {

	public ContainerBackpack(final EntityPlayer player, ItemInventoryBackpack inventory) {
		super(inventory, player);

		int lines = 0;
		int columns = 0;
		int startX = 0;
		int startY = 0;
		if (inventory.getSizeInventory() == Defaults.SLOTS_BACKPACK_DEFAULT) {
			lines = 3;
			columns = 5;
			startX = 44;
			startY = 19;
		} else if (inventory.getSizeInventory() == Defaults.SLOTS_BACKPACK_T2) {
			lines = 5;
			columns = 9;
			startX = 8;
			startY = 8;
		}

		// Inventory
		for (int j = 0; j < lines; j++) {
			for (int k = 0; k < columns; k++) {
				int slot = k + j * columns;
				addSlotToContainer(new SlotFiltered(inventory, slot, startX + k * 18, startY + j * 18));
			}
		}

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSecuredSlot(player.inventory, j + i * 9 + 9, 8 + j * 18, 11 + startY + lines * 18 + i * 18);
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSecuredSlot(player.inventory, i, 8 + i * 18, 11 + startY + lines * 18 + 58);
	}

}
