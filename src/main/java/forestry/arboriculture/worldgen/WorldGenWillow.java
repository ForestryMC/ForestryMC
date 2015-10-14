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

public class WorldGenWillow extends WorldGenTree {

	public WorldGenWillow(ITreeGenData tree) {
		super(tree, 5, 2);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth, 0.8f);
		generateSupportStems(world, height, girth, 0.2f, 0.2f);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1.5f, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 2.5f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 3f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 3f, 1, leaf);
		while (leafSpawn > 2) {
			// support branches for tall willows, keeps the leaves from decaying immediately
			if ((leafSpawn - 3) % 6 == 0) {
				generateBranches(world, leafSpawn, 0, 0, 0, 0, 2, 1);
			}
			generateCircle(world, new Vector(0f, leafSpawn--, 0f), 4f, 2, 1, leaf, EnumReplaceMode.NONE);
		}
		generateCircle(world, new Vector(0f, leafSpawn--, 0f), 4f, 1, 1, leaf, EnumReplaceMode.NONE);
		generateCircle(world, new Vector(0f, leafSpawn--, 0f), 4f, 1, 1, leaf, EnumReplaceMode.NONE);
		generateCircle(world, new Vector(0f, leafSpawn, 0f), 4f, 1, 1, leaf, 0.4f, EnumReplaceMode.NONE);

	}

}
