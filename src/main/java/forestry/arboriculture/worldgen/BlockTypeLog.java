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

public class BlockTypeLog extends BlockTypeWood {
	public BlockTypeLog(ItemStack itemStack) {
		super(itemStack);
	}

	@Override
	public void setDirection(ForgeDirection facing) {
		if (facing.offsetX != 0) {
			blockMeta = 4;
		} else if (facing.offsetZ != 0) {
			blockMeta = 8;
		} else {
			blockMeta = 0;
		}
	}
}
