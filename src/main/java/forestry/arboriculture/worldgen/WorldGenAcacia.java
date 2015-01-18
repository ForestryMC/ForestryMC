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
}
