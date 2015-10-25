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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class HiveGenTree extends HiveGen {

	@Override
	public boolean isValidLocation(World world, BlockPos pos) {
		Block blockAbove = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock();
		if (!blockAbove.isLeaves(world, new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()))) {
			return false;
		}

		// not a good location if right on top of something
		return canReplace(world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
	}

	@Override
	public int getYForHive(World world, BlockPos pos) {
		// get top leaf block
		pos = world.getHeight(pos);
		pos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
		if (!world.getBlockState(pos).getBlock().isLeaves(world, pos)) {
			return -1;
		}

		// get to the bottom of the leaves
		do {
			pos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
		} while (world.getBlockState(pos).getBlock().isLeaves(world, pos));

		return pos.getY();
	}
}
