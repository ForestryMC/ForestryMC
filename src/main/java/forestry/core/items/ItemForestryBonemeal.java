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
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemForestryBonemeal extends ItemForestry {
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float facingX, float facingY, float facingZ) {
		if (ItemDye.applyBonemeal(itemstack, world, x, y, z, player)) {
			if (!world.isRemote) {
				world.playAuxSFX(2005, x, y, z, 0);
			}

			return true;
		}
		return super.onItemUse(itemstack, player, world, x, y, z, par7, facingX, facingY, facingZ);
	}
}
