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
package forestry.greenhouse.multiblock;

import net.minecraft.util.math.BlockPos;

import forestry.greenhouse.api.greenhouse.Position2D;

public class GreenhouseLimitsBuilder {
	public BlockPos.MutableBlockPos maximumCoordinates;
	public BlockPos.MutableBlockPos minimumCoordinates;

	public GreenhouseLimitsBuilder() {
		this.maximumCoordinates = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		this.minimumCoordinates = new BlockPos.MutableBlockPos(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public void recalculate(BlockPos position) {
		if (position.getX() < minimumCoordinates.getX()) {
			minimumCoordinates.setPos(position.getX(), minimumCoordinates.getY(), minimumCoordinates.getZ());
		}
		if (position.getX() > maximumCoordinates.getX()) {
			maximumCoordinates.setPos(position.getX(), maximumCoordinates.getY(), maximumCoordinates.getZ());
		}
		if (position.getY() < minimumCoordinates.getY()) {
			minimumCoordinates.setPos(minimumCoordinates.getX(), position.getY(), minimumCoordinates.getZ());
		}
		if (position.getY() > maximumCoordinates.getY()) {
			maximumCoordinates.setPos(maximumCoordinates.getX(), position.getY(), maximumCoordinates.getZ());
		}
		if (position.getZ() < minimumCoordinates.getZ()) {
			minimumCoordinates.setPos(minimumCoordinates.getX(), minimumCoordinates.getY(), position.getZ());
		}
		if (position.getZ() > maximumCoordinates.getZ()) {
			maximumCoordinates.setPos(maximumCoordinates.getX(), maximumCoordinates.getY(), position.getZ());
		}
	}

	public GreenhouseLimits build(int height, int depth) {
		return new GreenhouseLimits(new Position2D(maximumCoordinates), new Position2D(minimumCoordinates), height, depth);
	}
}
