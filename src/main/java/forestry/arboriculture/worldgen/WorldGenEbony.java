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

public class WorldGenEbony extends WorldGenTree {

	public WorldGenEbony(ITreeGenData tree) {
		super(tree, 10, 4);
	}

	@Override
	public void generate(World world) {

		int offset = (girth - 1) / 2;
		int trunksgenerated = 0;

		for (int x = -offset; x < -offset + girth; x++) {
			for (int z = -offset; z < -offset + girth; z++) {
				if (world.rand.nextFloat() < 0.6f) {
					for (int i = 0; i < height; i++) {
						addWood(world, x, i, z, EnumReplaceMode.ALL);
						if (i > height / 2 && world.rand.nextFloat() < 0.1f * (10 / height)) {
							break;
						}
					}
					trunksgenerated++;
				} else {
					for (int i = 0; i < 1; i++) {
						clearBlock(world, x, i, z);
					}
				}
			}
		}

		// Generate backup trunk, if we failed to generate any.
		if (trunksgenerated <= 0) {
			generateTreeTrunk(world, height, 1, 0.6f);
		}

		// Add tree top
		for (int times = 0; times < 2 * height; times++) {
			int h = 2 * girth + world.rand.nextInt(height - girth);
			if (world.rand.nextBoolean() && h < height / 2) {
				h = height / 2 + world.rand.nextInt(height / 2);
			}

			int x_off = -(girth) + world.rand.nextInt(2 * girth);
			int y_off = -(girth) + world.rand.nextInt(2 * girth);

			Vector center = new Vector(x_off, h, y_off);
			int radius = 1 + world.rand.nextInt(girth);
			generateSphere(world, center, radius, leaf, EnumReplaceMode.NONE);
		}

	}

}
