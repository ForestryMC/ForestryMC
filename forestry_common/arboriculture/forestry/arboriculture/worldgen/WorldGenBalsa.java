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

public class WorldGenBalsa extends WorldGenTree {

	public WorldGenBalsa(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height;

		addLeaf(0, leafSpawn--, 0, EnumReplaceMode.NONE);
		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);

		if (height > 10)
			generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);

		leafSpawn--;

		while (leafSpawn > 6) {
			generateAdjustedCylinder(leafSpawn, 0, 1, leaf);
			leafSpawn--;
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(6, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log3, 3);
	}

}
