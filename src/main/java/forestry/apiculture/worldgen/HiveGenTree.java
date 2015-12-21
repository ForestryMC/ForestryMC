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
package forestry.apiculture.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class HiveGenTree extends HiveGen {

	@Override
	public boolean isValidLocation(World world, BlockPos pos) {
		Block blockAbove = world.getBlockState(pos.add(0, 1, 0)).getBlock();
		if (!blockAbove.isLeaves(world, pos.add(0, 1, 0))) {
			return false;
		}

		// not a good location if right on top of something
		return canReplace(world, pos.add(0, -1, 0));
	}

	@Override
	public int getYForHive(World world, int x, int z) {
		// get top leaf block
		int y = world.getHeight(new BlockPos(x, 0, z)).getY() - 1;
		if (!world.getBlockState(new BlockPos(x, y, z)).getBlock().isLeaves(world, new BlockPos(x, y, z))) {
			return -1;
		}

		// get to the bottom of the leaves
		do {
			y--;
		} while (world.getBlockState(new BlockPos(x, y, z)).getBlock().isLeaves(world,new BlockPos(x, y, z)));

		return y;
	}
}
