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

public class WorldGenSequoia extends WorldGenTree {

	public WorldGenSequoia(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int topLength = height / 4;

		int topHeight = height - topLength + rand.nextInt(height / 4);

		int leafSpawn = height + 2;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);

		while (leafSpawn > topHeight)
			generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);

		for (int times = 0; times < height / 4; times++) {
			int h = (height/3) + rand.nextInt(height - (height/3));
			if (rand.nextBoolean() && h < height / 3)
				h = height / 2 + rand.nextInt(height / 3);
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(2), leaf, EnumReplaceMode.NONE);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(20, 5);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log2, 3);
	}

}
