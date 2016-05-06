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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.multiblock.IFarmComponent;

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

	public static BlockPos getFarmMultiblockCorner(World world, BlockPos start, FarmDirection farmSide, FarmDirection layoutDirection) {
		BlockPos edge = getFarmMultiblockEdge(world, start, farmSide);
		return getFarmMultiblockEdge(world, edge, getOpposite(layoutDirection));
	}

	private static BlockPos getFarmMultiblockEdge(World world, BlockPos start, FarmDirection direction) {
		BlockPos.MutableBlockPos edge = new BlockPos.MutableBlockPos(start);

		while (world.getTileEntity(edge) instanceof IFarmComponent) {
			edge.offsetMutable(direction.getFacing());
		}

		FarmDirection oppositeDirection = getOpposite(direction);
		edge.offsetMutable(oppositeDirection.getFacing());
		return edge.toImmutable();
	}

}
