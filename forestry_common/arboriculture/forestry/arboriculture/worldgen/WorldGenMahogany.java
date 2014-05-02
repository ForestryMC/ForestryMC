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

public class WorldGenMahogany extends WorldGenTree {

	public WorldGenMahogany(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth, 0.6f);
		generateSupportStems(height, girth, 0.4f, 0.4f);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 3f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 3f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(12, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log3, 2);
	}

}
