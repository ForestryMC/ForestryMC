/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.worldgen;

import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class BlockTypeLeaf extends BlockType {

	private String owner;

	public BlockTypeLeaf(String owner) {
		super(ForestryBlock.leaves, 0);
	}

	@Override
	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		tree.setLeaves(world, owner, x, y, z);
	}
}
