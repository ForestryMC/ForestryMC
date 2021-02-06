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
package forestry.factory.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;

class FakeCraftingInventory {

	private static final Container EMPTY_CONTAINER = new Container(null, -1) {
		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}
	};

	public static CraftingInventory of(IInventory backing) {
		CraftingInventory inventory = new CraftingInventory(EMPTY_CONTAINER, 3, 3);

		for (int i = 0; i < 9; i++) {
			inventory.setInventorySlotContents(i, backing.getStackInSlot(i));
		}

		return inventory;
	}
}