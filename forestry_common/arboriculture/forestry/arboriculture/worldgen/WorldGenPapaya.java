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

public class WorldGenPapaya extends WorldGenTree {

	public WorldGenPapaya(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);
		
		int yCenter = height - girth;
		yCenter = yCenter > 3 ? yCenter : 4;
		generateSphere(getCenteredAt(yCenter, 0), 2 + rand.nextInt(girth), leaf, EnumReplaceMode.NONE);

	}
	
	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log5, 3);
	}

	@Override
	public void preGenerate() {
		height = determineHeight(7, 2);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

}
