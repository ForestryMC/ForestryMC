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
package forestry.mail.items;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.mail.gui.ContainerCatalogue;

public class ItemCatalogue extends ItemWithGui {

	public ItemCatalogue() {
		super((new Item.Properties()).tab(ItemGroupForestry.tabForestry));
	}

	@Nullable
	@Override
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		return new ContainerCatalogue(windowId, player.inventory);
	}
}
