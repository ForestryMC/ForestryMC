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

import com.google.common.collect.ImmutableSet;
import forestry.api.farming.FarmDirection;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class FarmHelper {

	public static final ImmutableSet<Block> bricks = ImmutableSet.of(
			Blocks.BRICK_BLOCK,
			Blocks.STONEBRICK,
			Blocks.SANDSTONE,
			Blocks.NETHER_BRICK,
			Blocks.QUARTZ_BLOCK
	);

	private static FarmDirection getOpposite(FarmDirection farmDirection) {
		EnumFacing forgeDirection = farmDirection.getFacing();
		EnumFacing forgeDirectionOpposite = forgeDirection.getOpposite();
		return FarmDirection.getFarmDirection(forgeDirectionOpposite);
	}

	/**
	 * @return the corner of the farm for the given side and layout. Returns null if the corner is not in a loaded chunk.
	 */
	public static BlockPos getFarmMultiblockCorner(BlockPos start, FarmDirection farmSide, FarmDirection layoutDirection, BlockPos minFarmCoord, BlockPos maxFarmCoord) {
		BlockPos edge = getFarmMultiblockEdge(start, farmSide, maxFarmCoord, minFarmCoord);
		return getFarmMultiblockEdge(edge, getOpposite(layoutDirection), maxFarmCoord, minFarmCoord);
	}

	/**
	 * @return the edge of the farm for the given starting point and direction.
	 */
	private static BlockPos getFarmMultiblockEdge(BlockPos start, FarmDirection direction, BlockPos maxFarmCoord, BlockPos minFarmCoord) {
		switch (direction) {
			case NORTH: // -z
				return new BlockPos(start.getX(), start.getY(), minFarmCoord.getZ());
			case EAST: // +x
				return new BlockPos(maxFarmCoord.getX(), start.getY(), start.getZ());
			case SOUTH: // +z
				return new BlockPos(start.getX(), start.getY(), maxFarmCoord.getZ());
			case WEST: // -x
				return new BlockPos(minFarmCoord.getX(), start.getY(), start.getZ());
			default:
				throw new IllegalArgumentException("Invalid farm direction: " + direction);
		}
	}
}
