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
package forestry.arboriculture.worldgen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class TreeBlockType implements ITreeBlockType {

	private final BlockState blockState;

	public TreeBlockType(BlockState blockState) {
		this.blockState = blockState;
	}

	@Override
	public void setDirection(Direction facing) {

	}

	@Override
	public boolean setBlock(LevelAccessor world, BlockPos pos) {
		return world.setBlock(pos, blockState, 18);
	}

	public BlockState getBlockState() {
		return blockState;
	}
}
