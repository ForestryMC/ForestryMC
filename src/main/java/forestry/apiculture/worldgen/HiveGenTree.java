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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;

public class HiveGenTree extends HiveGen {

	@Override
	public boolean isValidLocation(WorldGenLevel world, BlockPos pos) {
		BlockPos posAbove = pos.above();
		BlockState blockStateAbove = world.getBlockState(posAbove);
		if (!isTreeBlock(blockStateAbove, world, posAbove)) {
			return false;
		}

		// not a good location if right on top of something
		BlockPos posBelow = pos.below();
		BlockState blockStateBelow = world.getBlockState(posBelow);
		return canReplace(blockStateBelow, world, posBelow);
	}

	@Override
	public BlockPos getPosForHive(WorldGenLevel world, int x, int z) {
		// get top leaf block
		final BlockPos topPos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(x, 0, z)).below();
		if (topPos.getY() <= 0) {
			return null;
		}

		BlockState blockState = world.getBlockState(topPos);
		if (!isTreeBlock(blockState, world, topPos)) {
			return null;
		}

		// get to the bottom of the leaves
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		pos.set(topPos);
		do {
			pos.move(Direction.DOWN);
			blockState = world.getBlockState(pos);
		} while (isTreeBlock(blockState, world, pos));

		return pos.immutable();
	}

}
