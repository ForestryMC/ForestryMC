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
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.hives.IHiveGen;
import forestry.core.utils.BlockUtil;

public abstract class HiveGen implements IHiveGen {

	public static boolean isTreeBlock(IBlockState blockState, World world, BlockPos pos) {
		Block block = blockState.getBlock();
		return block.isLeaves(blockState, world, pos) || block.isWood(world, pos);
	}

	@Override
	public boolean canReplace(IBlockState blockState, World world, BlockPos pos) {
		return BlockUtil.canReplace(blockState, world, pos);
	}
}
