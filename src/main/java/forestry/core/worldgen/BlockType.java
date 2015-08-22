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
package forestry.core.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

public class BlockType {

	private final Block block;
	private final int meta;

	public BlockType(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	public BlockType(ForestryBlock block, int meta) {
		this.block = block.block();
		this.meta = meta;
	}

	public void setBlock(World world, ITreeGenData tree, BlockPos pos) {
		world.setBlockState(pos, block.getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH);
	}
	
	public int getMeta() {
		return this.meta;
	}
	
	public Block getBlock() {
		return this.block;
	}
}
