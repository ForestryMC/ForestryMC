/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.worldgen;

import forestry.api.world.ITreeGenData;
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class WorldGenPoplar extends WorldGenTree {

	public WorldGenPoplar(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		while (leafSpawn > girth - 1)
			generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);

	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log5, 1);
	}

	@Override
	public void preGenerate() {
		height = determineHeight(8, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

}
