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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.items.ItemWithGui;

public class ItemImprinter extends ItemWithGui {
	public ItemImprinter() {
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiImprinter(player.inventory, new ItemInventoryImprinter(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerImprinter(player.inventory, new ItemInventoryImprinter(player, heldItem));
	}
}
