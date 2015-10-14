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

import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenBaobab extends WorldGenTree {

	public WorldGenBaobab(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height - 1, girth);

		List<ChunkCoordinates> branchCoords = generateBranches(world, height, 0, 0, 0, 0.5f, 4, 6);
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 0.0f, 2, leaf, EnumReplaceMode.NONE);
		}

		if (world.rand.nextFloat() < 0.3f) {
			generateCylinder(world, new Vector(0, height - 1, 0), girth, 1, wood, EnumReplaceMode.SOFT);
		} else if (world.rand.nextBoolean()) {
			generateCylinder(world, new Vector(0, height - 1, 0), girth - 1, 1, wood, EnumReplaceMode.SOFT);
		}

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1f, 1, leaf);

		// Add tree top
		for (int times = 0; times < height / 2; times++) {
			int h = height - 1 + world.rand.nextInt(4);
			if (world.rand.nextBoolean() && h < height / 2) {
				h = height / 2 + world.rand.nextInt(height / 2);
			}

			int x_off = -girth + world.rand.nextInt(2 * girth);
			int y_off = -girth + world.rand.nextInt(2 * girth);

			Vector center = new Vector(x_off, h, y_off);
			int radius = 1;
			if (girth > 1) {
				radius += world.rand.nextInt(girth - 1);
			}
			generateSphere(world, center, radius, leaf, EnumReplaceMode.NONE);
		}

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int delim = modifyByHeight(world, 6, 0, height);
			int h = delim + (delim < height ? world.rand.nextInt(height - delim) : 0);
			if (world.rand.nextBoolean() && h < height / 2) {
				h = height / 2 + world.rand.nextInt(height / 2);
			}
			int x_off = -1 + world.rand.nextInt(3);
			int y_off = -1 + world.rand.nextInt(3);

			Vector center = new Vector(x_off, h, y_off);
			int radius = 1 + world.rand.nextInt(2);
			generateSphere(world, center, radius, leaf, EnumReplaceMode.NONE);
		}

	}

}
