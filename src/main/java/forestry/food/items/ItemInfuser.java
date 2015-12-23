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
package forestry.food.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.items.ItemWithGui;
import forestry.food.gui.ContainerInfuser;
import forestry.food.gui.GuiInfuser;
import forestry.food.inventory.ItemInventoryInfuser;

public class ItemInfuser extends ItemWithGui {
	@Override
	public Object getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiInfuser(player.inventory, new ItemInventoryInfuser(player, heldItem));
	}

	@Override
	public Object getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerInfuser(player.inventory, new ItemInventoryInfuser(player, heldItem));
	}
}
