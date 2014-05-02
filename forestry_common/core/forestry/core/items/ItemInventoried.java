/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerItemInventory;
import forestry.core.interfaces.IInventoriedItem;

public abstract class ItemInventoried extends ItemForestry implements IInventoriedItem {

	public ItemInventoried() {
		super();
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		if (itemstack != null &&
				player instanceof EntityPlayerMP &&
				player.openContainer instanceof ContainerItemInventory)
			((EntityPlayerMP) player).closeScreen();

		return super.onDroppedByPlayer(itemstack, player);
	}
}
