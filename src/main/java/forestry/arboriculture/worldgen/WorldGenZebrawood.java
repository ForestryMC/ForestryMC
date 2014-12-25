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

public class WorldGenZebrawood extends WorldGenTree {

	public WorldGenZebrawood(ITreeGenData tree) {
		super(tree);
	}
	
	@Override
	public void generate() {

		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);

		while (leafSpawn > height - 4)
			generateAdjustedCylinder(leafSpawn--, 2.5f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int h = 10 + rand.nextInt(Math.max(1, height - 10));
			if (rand.nextBoolean() && h < height / 2)
				h = height / 2 + rand.nextInt(height / 2);
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);
			generateSphere(new Vector(x_off, h, y_off), 1 + rand.nextInt(1), leaf, EnumReplaceMode.NONE);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(8, 8);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
