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
package forestry.arboriculture.worldgen;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.ForgeDirection;

public class BlockTypeWoodStairs extends BlockTypeWood {
	public BlockTypeWoodStairs(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public void setDirection(ForgeDirection facing) {
		switch (facing) {
			case NORTH:
				blockMeta = 3;
				break;
			case SOUTH:
				blockMeta = 2;
				break;
			case WEST:
				blockMeta = 1;
				break;
			default:
				blockMeta = 0;
				break;
		}
	}
}
