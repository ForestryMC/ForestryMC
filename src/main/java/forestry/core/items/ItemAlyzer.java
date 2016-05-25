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

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.utils.ItemTooltipUtil;

public class ItemAlyzer extends ItemWithGui {
	public ItemAlyzer() {
		setCreativeTab(Tabs.tabApiculture);
	}
	
	@Override
	public void openGui(EntityPlayer entityplayer) {
		super.openGui(entityplayer);
	}

	@Override
	public Object getGui(@Nonnull EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiAlyzer(player, new ItemInventoryAlyzer(player, heldItem));
	}

	@Override
	public Object getContainer(@Nonnull EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerAlyzer(new ItemInventoryAlyzer(player, heldItem), player);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		ItemTooltipUtil.addInformation(stack, playerIn, tooltip, advanced);
	}
}
