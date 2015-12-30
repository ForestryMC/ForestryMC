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
package forestry.lepidopterology.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.items.ItemWithGui;
import forestry.lepidopterology.gui.GuiFlutterlyzer;
import forestry.lepidopterology.inventory.ItemInventoryFlutterlyzer;

public class ItemFlutterlyzer extends ItemWithGui {
	public ItemFlutterlyzer() {
		setCreativeTab(Tabs.tabLepidopterology);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiFlutterlyzer(player, new ItemInventoryFlutterlyzer(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerAlyzer(new ItemInventoryFlutterlyzer(player, heldItem), player);
	}
}
