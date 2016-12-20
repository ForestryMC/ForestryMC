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
package forestry.core.items;

import forestry.api.core.Tabs;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public void openGui(EntityPlayer entityplayer) {
		super.openGui(entityplayer);
	}

	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiAlyzer(player, new ItemInventoryAlyzer(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerAlyzer(new ItemInventoryAlyzer(player, heldItem), player);
	}
}
