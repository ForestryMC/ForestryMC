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

public class WorldGenAcacia extends WorldGenTree {

	public WorldGenAcacia(ITreeGenData tree) {
		super(tree, 5, 2);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		float rnd = world.rand.nextFloat();
		int offset = 0;
		if (rnd > 0.6f) {
			offset = world.rand.nextInt(girth);
		} else if (rnd > 0.3) {
			offset = -world.rand.nextInt(girth);
		}


		generateAdjustedCylinder(world, leafSpawn--, offset, 0, 1, leaf, EnumReplaceMode.NONE);
		generateAdjustedCylinder(world, leafSpawn--, offset, 1.5f, 1, leaf, EnumReplaceMode.NONE);

		if (world.rand.nextBoolean()) {
			generateAdjustedCylinder(world, leafSpawn--, offset, 3.9f, 1, leaf, EnumReplaceMode.NONE);
		} else {
			generateAdjustedCylinder(world, leafSpawn--, offset, 2.9f, 1, leaf, EnumReplaceMode.NONE);
		}
	}

}
