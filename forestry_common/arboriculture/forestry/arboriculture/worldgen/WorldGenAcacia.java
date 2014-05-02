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

public class WorldGenAcacia extends WorldGenTree {

	public WorldGenAcacia(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		float rnd = world.rand.nextFloat();
		int offset = 0;
		if(rnd > 0.6f)
			offset = rand.nextInt(girth);
		else if(rnd > 0.3)
			offset = -rand.nextInt(girth);


		generateAdjustedCylinder(leafSpawn--, offset, 0, 1, leaf, EnumReplaceMode.NONE);
		generateAdjustedCylinder(leafSpawn--, offset, 1.5f, 1, leaf, EnumReplaceMode.NONE);

		if (rand.nextBoolean())
			generateAdjustedCylinder(leafSpawn--, offset, 3.9f, 1, leaf, EnumReplaceMode.NONE);
		else
			generateAdjustedCylinder(leafSpawn--, offset, 2.9f, 1, leaf, EnumReplaceMode.NONE);
	}

	@Override
	public void preGenerate() {
		height = determineHeight(5, 2);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log1, 2);
	}

}
