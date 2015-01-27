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

public class WorldGenChestnut extends WorldGenTree {

	public WorldGenChestnut(ITreeGenData tree) {
		super(tree);

		minHeight = 7;
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

		while (leafSpawn > 4) {
			generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);
		}
		if (rand.nextBoolean()) {
			generateAdjustedCylinder(leafSpawn--, 2f, 1, leaf);
		}
		generateAdjustedCylinder(leafSpawn--, 1.5f, 1, leaf);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(7, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
