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
package forestry.farming;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;

public class FarmTarget {

	private final BlockPos start;
	private final FarmDirection direction;
	private final int limit;

	private int yOffset;
	private int extent;

	public FarmTarget(BlockPos start, FarmDirection direction, int limit) {
		this.start = start;
		this.direction = direction;
		this.limit = limit;
	}

	public BlockPos getStart() {
		return start;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public int getExtent() {
		return extent;
	}

	public FarmDirection getDirection() {
		return direction;
	}

	public void setExtentAndYOffset(World world, BlockPos platformPosition) {
		if (platformPosition == null) {
			extent = 0;
			return;
		}

		BlockPos position = new BlockPos(platformPosition);
		for (extent = 0; extent < limit; extent++) {
			Block platform = world.getBlockState(position).getBlock();
			if (!FarmHelper.bricks.contains(platform)) {
				break;
			}
			position = position.offset(getDirection().getFacing());
		}

		yOffset = platformPosition.getY() + 1 - getStart().getY();
	}
}
