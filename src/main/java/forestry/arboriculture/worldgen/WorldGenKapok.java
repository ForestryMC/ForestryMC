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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenKapok extends WorldGenTree {

	public WorldGenKapok(ITreeGenData tree) {
		super(tree, 10, 8);
	}

	@Override
	public void generate(World world) {

		generateTreeTrunk(world, height, girth, 0.6f);
		generateSupportStems(world, height, girth, 0.8f, 0.4f);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.9f, 1, leaf);

		List<ChunkCoordinates> branchCoords = new ArrayList<>();
		while (leafSpawn > height - 4) {
			int radius = Math.round(girth * (height - leafSpawn) / 1.5f) + 6;
			branchCoords.addAll(generateBranches(world, leafSpawn, 0, 0, 0.3f, 0.25f, radius, 6));
			leafSpawn -= 2;
		}

		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY + 1, branchEnd.posX, branchEnd.posZ, 2.0f, 2, leaf, EnumReplaceMode.NONE);
		}

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
