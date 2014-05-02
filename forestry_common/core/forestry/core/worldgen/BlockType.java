/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.worldgen;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.config.Defaults;

public class BlockType {

	Block block;
	int meta;

	public BlockType(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		world.setBlock(x, y, z, block, meta, Defaults.FLAG_BLOCK_SYNCH);
		if (world.getTileEntity(x, y, z) != null)
			world.removeTileEntity(x, y, z);
	}
}
