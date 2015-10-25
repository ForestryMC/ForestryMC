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

public class WorldGenSequoia extends WorldGenTree {

	public WorldGenSequoia(ITreeGenData tree) {
		this(tree, 20, 5);
	}

	protected WorldGenSequoia(ITreeGenData tree, int baseHeight, int heightVariation) {
		super(tree, baseHeight, heightVariation);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int topLength = height / 4;

		int topHeight = height - topLength + world.rand.nextInt(height / 4);

		int leafSpawn = height + 2;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);

		while (leafSpawn > topHeight) {
			generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);
		}

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);

		for (int times = 0; times < height / 4; times++) {
			int h = (height / 3) + world.rand.nextInt(height - (height / 3));
			if (world.rand.nextBoolean() && h < height / 3) {
				h = height / 2 + world.rand.nextInt(height / 3);
			}
			int x_off = -1 + world.rand.nextInt(3);
			int y_off = -1 + world.rand.nextInt(3);

			Vector center = new Vector(x_off, h, y_off);
			int radius = 1 + world.rand.nextInt(2);
			generateSphere(world, center, radius, leaf, EnumReplaceMode.NONE);
		}
	}

}
