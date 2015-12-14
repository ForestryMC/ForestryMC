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

import net.minecraft.init.Blocks;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.worldgen.BlockType;

public class BlockTypeVanillaStairs extends BlockType {
	public BlockTypeVanillaStairs(int meta) {
		super(Blocks.oak_stairs, meta);
	}

	@Override
	public void setDirection(ForgeDirection facing) {
		switch (facing) {
			case NORTH:
				meta = 3;
				break;
			case SOUTH:
				meta = 2;
				break;
			case WEST:
				meta = 1;
				break;
			default:
				meta = 0;
				break;
		}
	}
}
