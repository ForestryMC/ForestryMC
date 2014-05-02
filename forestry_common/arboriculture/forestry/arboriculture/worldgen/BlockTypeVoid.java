/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.worldgen;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.BlockType;

public class BlockTypeVoid extends BlockType {

	public BlockTypeVoid() {
		super(Blocks.air, 0);
	}

	@Override
	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		world.setBlockToAir(x, y, z);
		if (world.getTileEntity(x, y, z) != null)
			world.removeTileEntity(x, y, z);
	}

}
