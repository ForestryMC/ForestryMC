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

import forestry.api.world.ITreeGenData;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import net.minecraft.block.Block;
import net.minecraft.world.World;

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

	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		world.setBlock(x, y, z, block, meta, Defaults.FLAG_BLOCK_SYNCH);
		if (world.getTileEntity(x, y, z) != null)
			world.removeTileEntity(x, y, z);
	}
}
