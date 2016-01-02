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
package forestry.core.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public final class BlockPosUtil {
	public static boolean isAirBlock(World world, BlockPos position) {
		return world.isAirBlock(position);
	}

	public static boolean isWoodBlock(World world, BlockPos position) {
		Block block = getBlock(world, position);
		return block.isWood(world, position);
	}

	public static TileEntity getTile(World world, BlockPos position) {
		return world.getTileEntity(position);
	}

	public static Block getBlock(World world, BlockPos position) {
		return getBlockState(world, position).getBlock();
	}
	
	public static IBlockState getBlockState(World world, BlockPos position) {
		return world.getBlockState(position);
	}

	public static int getBlockMeta(World world, BlockPos position) {
		return getBlock(world, position).getMetaFromState(getBlockState(world, position));
	}

	public static ItemStack getAsItemStack(World world, BlockPos position) {
		return new ItemStack(getBlock(world, position), 1, getBlockMeta(world, position));
	}
}