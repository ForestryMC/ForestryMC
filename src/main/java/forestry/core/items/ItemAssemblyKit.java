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

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAssemblyKit extends ItemForestry {
	private final ItemStack assembled;

	public ItemAssemblyKit(ItemStack assembled) {
		maxStackSize = 24;
		this.assembled = assembled;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			itemstack.stackSize--;
			EntityItem entity = new EntityItem(world, entityplayer.posX, entityplayer.posY, entityplayer.posZ, assembled.copy());
			world.spawnEntityInWorld(entity);
		}
		return itemstack;
	}
}
