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

import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenIpe extends WorldGenTree {

	public WorldGenIpe(ITreeGenData tree) {
		super(tree, 8, 8);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;
		float adjustedGirth = girth * .65f;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.2f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.2f * adjustedGirth, 1, leaf);

		while (leafSpawn > 7) {
			generateAdjustedCylinder(world, leafSpawn, (float) (1.25f * (adjustedGirth * .65)), 1, leaf);
			leafSpawn--;
		}

		generateAdjustedCylinder(world, leafSpawn--, 1.6f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1.6f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1.6f * adjustedGirth, 1, leaf);

		if (world.rand.nextBoolean()) {
			generateAdjustedCylinder(world, leafSpawn--, 1.25f * adjustedGirth, 1, leaf);
		}

		generateAdjustedCylinder(world, leafSpawn--, 1f * adjustedGirth, 1, leaf);

	}

}
