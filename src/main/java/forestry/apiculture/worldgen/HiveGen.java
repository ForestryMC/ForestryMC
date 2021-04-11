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
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import forestry.api.apiculture.hives.IHiveGen;
import forestry.core.utils.BlockUtil;

public abstract class HiveGen implements IHiveGen {

	public static boolean isTreeBlock(BlockState blockState, ISeedReader world, BlockPos pos) {
		Block block = blockState.getBlock();
		return block.is(BlockTags.LEAVES) || block.is(BlockTags.LOGS);
	}

	@Override
	public boolean canReplace(BlockState blockState, ISeedReader world, BlockPos pos) {
		return BlockUtil.canReplace(blockState, world, pos);
	}
}
