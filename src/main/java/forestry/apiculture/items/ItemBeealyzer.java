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
import forestry.apiculture.gui.GuiBeealyzer;
import forestry.apiculture.inventory.ItemInventoryBeealyzer;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.items.ItemWithGui;

public class ItemBeealyzer extends ItemWithGui {
	public ItemBeealyzer() {
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		ItemInventoryBeealyzer inventory = new ItemInventoryBeealyzer(player, heldItem);
		return new GuiBeealyzer(player, inventory);
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		ItemInventoryBeealyzer inventory = new ItemInventoryBeealyzer(player, heldItem);
		return new ContainerAlyzer(inventory, player);
	}
}
