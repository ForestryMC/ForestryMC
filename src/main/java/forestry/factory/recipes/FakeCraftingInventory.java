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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

class FakeCraftingInventory {

	private static final AbstractContainerMenu EMPTY_CONTAINER = new AbstractContainerMenu(null, -1) {
		@Override
		public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean stillValid(Player playerIn) {
			return true;
		}
	};

	public static CraftingContainer of(Container backing) {
		CraftingContainer inventory = new CraftingContainer(EMPTY_CONTAINER, 3, 3);

		for (int i = 0; i < 9; i++) {
			inventory.setItem(i, backing.getItem(i));
		}

		return inventory;
	}
}
