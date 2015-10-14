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

public class WorldGenZebrawood extends WorldGenTree {

	public WorldGenZebrawood(ITreeGenData tree) {
		super(tree, 8, 8);
	}
	
	@Override
	public void generate(World world) {

		generateTreeTrunk(world, height, girth);
		generateSupportStems(world, height, girth, 0.8f, 0.3f);

		List<ChunkCoordinates> branchCoords = generateBranches(world, height - 4, 0, 0, 0, 0.25f, 3, 2, 0.75f);
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 1.0f, 2, leaf, EnumReplaceMode.NONE);
		}

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.9f, 1, leaf);

		while (leafSpawn > height - 4) {
			generateAdjustedCylinder(world, leafSpawn--, 2.5f, 1, leaf);
		}
		generateAdjustedCylinder(world, leafSpawn, 1.9f, 1, leaf);

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int h = 10 + world.rand.nextInt(Math.max(1, height - 10));
			if (world.rand.nextBoolean() && h < height / 2) {
				h = height / 2 + world.rand.nextInt(height / 2);
			}
			int x_off = -1 + world.rand.nextInt(3);
			int y_off = -1 + world.rand.nextInt(3);
			generateSphere(world, new Vector(x_off, h, y_off), 1 + world.rand.nextInt(1), leaf, EnumReplaceMode.NONE);
		}

	}

}
