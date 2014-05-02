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

public class WorldGenGiganteum extends WorldGenSequoia {

	public WorldGenGiganteum(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		super.generate();
		//generateSupportStems(height, girth, 0.8f, 0.6f);
	}

	@Override
	public void preGenerate() {
		height = determineHeight(35, 15);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log7, 0);
	}

}
