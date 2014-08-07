/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.worldgen;

import forestry.api.world.ITreeGenData;
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class WorldGenWillow extends WorldGenTree {

	public WorldGenWillow(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {

		generateTreeTrunk(height, girth, 0.8f);
		generateSupportStems(height, girth, 0.2f, 0.2f);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.5f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 2.5f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 3f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 3f, 1, leaf);
		while (leafSpawn > 2)
			generateCircle(new Vector(0f, leafSpawn--, 0f), 4f, 2, 1, leaf, EnumReplaceMode.NONE);
		generateCircle(new Vector(0f, leafSpawn--, 0f), 4f, 1, 1, leaf, EnumReplaceMode.NONE);
		generateCircle(new Vector(0f, leafSpawn--, 0f), 4f, 1, 1, leaf, EnumReplaceMode.NONE);
		generateCircle(new Vector(0f, leafSpawn--, 0f), 4f, 1, 1, leaf, 0.4f, EnumReplaceMode.NONE);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(5, 2);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log4, 0);
	}

}
