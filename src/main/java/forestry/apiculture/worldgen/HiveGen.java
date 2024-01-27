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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.apiculture.hives.IHiveGen;
import forestry.core.utils.BlockUtil;

public abstract class HiveGen implements IHiveGen {

	public static boolean isTreeBlock(BlockState blockState) {
		return !blockState.isAir();
	}

	@Override
	public boolean canReplace(BlockState blockState, WorldGenLevel world, BlockPos pos) {
		return BlockUtil.canReplace(blockState, world, pos);
	}
}
