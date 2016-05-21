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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeBlockType implements ITreeBlockType {

	private final IBlockState blockState;

	public TreeBlockType(IBlockState blockState) {
		this.blockState = blockState;
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}

	@Override
	public boolean setBlock(World world, BlockPos pos) {
		return world.setBlockState(pos, blockState);
	}

	public IBlockState getBlockState() {
		return blockState;
	}
}
