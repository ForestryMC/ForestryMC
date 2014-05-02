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

public class WorldGenTeak extends WorldGenTree {

	public WorldGenTeak(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);
		if (rand.nextBoolean())
			generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(6, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log1, 1);
	}

}
