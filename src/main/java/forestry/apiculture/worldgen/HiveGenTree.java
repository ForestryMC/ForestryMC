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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HiveGenTree extends HiveGen {

	@Override
	public boolean isValidLocation(World world, BlockPos pos) {
		BlockPos posAbove = pos.up();
		IBlockState blockStateAbove = world.getBlockState(posAbove);
		if (!isTreeBlock(blockStateAbove, world, posAbove)) {
			return false;
		}

		// not a good location if right on top of something
		BlockPos posBelow = pos.down();
		IBlockState blockStateBelow = world.getBlockState(posBelow);
		return canReplace(blockStateBelow, world, posBelow);
	}

	@Override
	public BlockPos getPosForHive(World world, int x, int z) {
		// get top leaf block
		final BlockPos topPos = world.getHeight(new BlockPos(x, 0, z)).down();
		if (topPos.getY() <= 0) {
			return null;
		}

		IBlockState blockState = world.getBlockState(topPos);
		if (!isTreeBlock(blockState, world, topPos)) {
			return null;
		}

		// get to the bottom of the leaves
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(topPos);
		do {
			pos.move(EnumFacing.DOWN);
			blockState = world.getBlockState(pos);
		} while (isTreeBlock(blockState, world, pos));

		return pos.toImmutable();
	}

}
