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

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;

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

	public void setExtentAndYOffset(World world, @Nullable BlockPos platformPosition, IFarmHousing housing) {
		if (platformPosition == null) {
			extent = 0;
			return;
		}

		BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(platformPosition);
		for (extent = 0; extent < limit; extent++) {
			if (!world.isBlockLoaded(position)) {
				break;
			}
			if (!housing.isValidPlatform(world, position)) {
				break;
			}
			position.move(getDirection().getFacing());
		}

		yOffset = platformPosition.getY() + 1 - getStart().getY();
	}
}
