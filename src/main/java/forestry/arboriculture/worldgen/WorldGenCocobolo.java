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
			int offset = 1;
			if (leafSpawn % 2 == 0) {
				if (rand.nextBoolean())
					offset = -1; 
				generateAdjustedCylinder(leafSpawn, offset, 2, 1, leaf, EnumReplaceMode.NONE);
			} else {
				if (rand.nextBoolean())
					offset = -1;
				generateAdjustedCylinder(leafSpawn, offset, 0, 1, leaf, EnumReplaceMode.NONE );
			}
			leafSpawn--;
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(8, 8);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
