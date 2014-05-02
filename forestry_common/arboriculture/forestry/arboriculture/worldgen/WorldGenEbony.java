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

public class WorldGenEbony extends WorldGenTree {

	public WorldGenEbony(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {

		int offset = (girth - 1) / 2;
		int trunksgenerated = 0;

		for (int x = -offset; x < -offset + girth; x++)
			for (int z = -offset; z < -offset + girth; z++)
				if (rand.nextFloat() < 0.6f) {
					for (int i = 0; i < height; i++) {
						addWood(x, i, z, EnumReplaceMode.ALL);
						if (i > height / 2 && rand.nextFloat() < 0.1f * (10 / height))
							break;
					}
					trunksgenerated++;
				} else
					for (int i = 0; i < 1; i++)
						clearBlock(x, i, z);

		// Generate backup trunk, if we failed to generate any.
		if (trunksgenerated <= 0)
			generateTreeTrunk(height, 1, 0.6f);

		// Add tree top
		for (int times = 0; times < 2 * height; times++) {
			int h = 2 * girth + rand.nextInt(height - girth);
			if (rand.nextBoolean() && h < height / 2)
				h = height / 2 + rand.nextInt(height / 2);

			int x_off = -(girth) + rand.nextInt(2 * girth);
			int y_off = -(girth) + rand.nextInt(2 * girth);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(girth), leaf, EnumReplaceMode.NONE);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(10, 4);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log3, 1);
	}

}
