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

public class WorldGenMaple extends WorldGenTree {

	public WorldGenMaple(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;
		float diameterchange = (float) 1 / height;
		int leafSpawned = 2;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);

		while (leafSpawn > 1) {
			generateAdjustedCylinder(leafSpawn--, 3 * diameterchange * leafSpawned, 1, leaf);
			generateAdjustedCylinder(leafSpawn--, 2 * diameterchange * leafSpawned, 1, leaf);
			leafSpawned += 2;
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(7, 5);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
