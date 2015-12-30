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
package forestry.arboriculture.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.arboriculture.gui.GuiTreealyzer;
import forestry.arboriculture.inventory.ItemInventoryTreealyzer;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.items.ItemWithGui;

public class ItemTreealyzer extends ItemWithGui {
	public ItemTreealyzer() {
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiTreealyzer(player, new ItemInventoryTreealyzer(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerAlyzer(new ItemInventoryTreealyzer(player, heldItem), player);
	}
}
