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

public class WorldGenChestnut extends WorldGenTree {

	public WorldGenChestnut(ITreeGenData tree) {
		super(tree);

		minHeight = 7;
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

		while (leafSpawn > 4)
			generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);
		if (rand.nextBoolean())
			generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.5f, 1, leaf);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(7, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log2, 0);
	}

}
