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
package forestry.apiculture.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import forestry.api.core.ItemGroups;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.items.ItemWithGui;

public class ItemImprinter extends ItemWithGui {
	public ItemImprinter() {
		super((new Item.Properties()).tab(ItemGroups.tabApiculture).stacksTo(1));
	}

	@Override
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		return new ContainerImprinter(windowId, player.getInventory(), new ItemInventoryImprinter(player, heldItem));
	}
}
