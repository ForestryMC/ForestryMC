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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.multiblock.IFarmComponent;

public class FarmHelper {

	public static final ImmutableSet<Block> bricks = ImmutableSet.of(
			Blocks.brick_block,
			Blocks.stonebrick,
			Blocks.sandstone,
			Blocks.nether_brick,
			Blocks.quartz_block
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
		BlockPos edge = new BlockPos(start);

		while (world.getTileEntity(edge) instanceof IFarmComponent) {
			edge = edge.offset(direction.getFacing());
		}

		edge = edge.offset(direction.getFacing());
		return new BlockPos(edge);
	}

}
