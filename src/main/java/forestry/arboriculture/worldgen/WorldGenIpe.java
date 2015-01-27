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

public class WorldGenIpe extends WorldGenTree {

	public WorldGenIpe(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;
		float adjustedGirth = girth * .65f;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.2f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.2f * adjustedGirth, 1, leaf);
		
		while (leafSpawn > 7) {
			generateAdjustedCylinder(leafSpawn, (float) (1.25f * (adjustedGirth * .65)), 1, leaf);
			leafSpawn--;
		}
		
		generateAdjustedCylinder(leafSpawn--, 1.6f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.6f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.6f * adjustedGirth, 1, leaf);
		
		if (rand.nextBoolean()) {
			generateAdjustedCylinder(leafSpawn--, 1.25f * adjustedGirth, 1, leaf);
		}

		generateAdjustedCylinder(leafSpawn--, 1f * adjustedGirth, 1, leaf);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(8, 8);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
