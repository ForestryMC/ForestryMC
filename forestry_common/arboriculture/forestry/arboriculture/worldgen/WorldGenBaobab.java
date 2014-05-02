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

public class WorldGenBaobab extends WorldGenTree {

	public WorldGenBaobab(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height - 1, girth);

		if (rand.nextFloat() < 0.3f)
			generateCylinder(new Vector(0, height - 1, 0), girth, 1, wood, EnumReplaceMode.NONE);
		else if (rand.nextBoolean())
			generateCylinder(new Vector(0, height - 1, 0), girth - 1, 1, wood, EnumReplaceMode.NONE);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1f, 1, leaf);

		// Add tree top
		for (int times = 0; times < height / 2; times++) {
			int h = height - 1 + rand.nextInt(4);
			if (rand.nextBoolean() && h < height / 2)
				h = height / 2 + rand.nextInt(height / 2);

			int x_off = -girth + rand.nextInt(2 * girth);
			int y_off = -girth + rand.nextInt(2 * girth);
			generateSphere(new Vector(x_off, h, y_off), 1 + (girth > 1 ? rand.nextInt(girth - 1) : 0), leaf, EnumReplaceMode.NONE);
		}

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int delim = modifyByHeight(6, 0, height);
			int h = delim + (delim < height ? rand.nextInt(height - delim) : 0);
			if (rand.nextBoolean() && h < height / 2)
				h = height / 2 + rand.nextInt(height / 2);
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(2), leaf, EnumReplaceMode.NONE);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(6, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log2, 2);
	}

}
