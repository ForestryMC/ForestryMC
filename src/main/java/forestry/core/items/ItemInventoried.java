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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.interfaces.IInventoriedItem;

public abstract class ItemInventoried extends ItemForestry implements IInventoriedItem {

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		if (itemstack != null && player instanceof EntityPlayerMP
				&& player.openContainer instanceof ContainerItemInventory) {
			player.closeScreen();
		}

		return super.onDroppedByPlayer(itemstack, player);
	}
}
