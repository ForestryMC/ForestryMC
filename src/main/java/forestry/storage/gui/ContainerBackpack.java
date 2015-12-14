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
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.storage.inventory.ItemInventoryBackpack;

public class ContainerBackpack extends ContainerItemInventory<ItemInventoryBackpack> {

	public enum Size {
		DEFAULT(3, 5, 44, 19),
		T2(5, 9, 8, 8);

		final int rows;
		final int columns;
		final int startX;
		final int startY;

		Size(int rows, int columns, int startX, int startY) {
			this.rows = rows;
			this.columns = columns;
			this.startX = startX;
			this.startY = startY;
		}

		public int getSize() {
			return rows * columns;
		}
	}

	public ContainerBackpack(EntityPlayer player, Size size, ItemStack parent) {
		super(new ItemInventoryBackpack(player, size.getSize(), parent), player.inventory, 8, 11 + size.startY + size.rows * 18);

		// Inventory
		for (int j = 0; j < size.rows; j++) {
			for (int k = 0; k < size.columns; k++) {
				int slot = k + j * size.columns;
				addSlotToContainer(new SlotFilteredInventory(inventory, slot, size.startX + k * 18, size.startY + j * 18));
			}
		}
	}
}
