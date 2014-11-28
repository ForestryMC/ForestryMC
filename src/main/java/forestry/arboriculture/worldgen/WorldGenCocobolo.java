/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.worldgen;

import forestry.api.world.ITreeGenData;
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class WorldGenCocobolo extends WorldGenTree {

	public WorldGenCocobolo(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height;

		addLeaf(0, leafSpawn--, 0, EnumReplaceMode.NONE);
		generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);

		if (height > 10)
			generateAdjustedCylinder(leafSpawn--, 2, 1, leaf);
			generateAdjustedCylinder(leafSpawn, 0, 1, leaf);
		
		leafSpawn--;
		
		while (leafSpawn > 4) {
			if (leafSpawn % 2 == 0) 
				generateAdjustedCylinder(leafSpawn, 2, 1, leaf);
			else 
				generateAdjustedCylinder(leafSpawn, 0, 1, leaf);
			leafSpawn--;
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(4, 6);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
