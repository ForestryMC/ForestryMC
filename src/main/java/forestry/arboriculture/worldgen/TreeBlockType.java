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

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.config.Constants;

public class TreeBlockType implements ITreeBlockType {

	private final Block block;
	private final int meta;

	public TreeBlockType(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}

	@Override
	public void setBlock(World world, ITreeGenData tree, BlockPos pos) {
		world.setBlockState(pos, block.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH);
	}

	public int getMeta() {
		return this.meta;
	}

	public Block getBlock() {
		return this.block;
	}
}
