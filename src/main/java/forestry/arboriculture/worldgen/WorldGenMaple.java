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

public class WorldGenMaple extends WorldGenTree {

	public WorldGenMaple(ITreeGenData tree) {
		super(tree, 7, 5);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		List<ChunkCoordinates> branchCoords = new ArrayList<>();
		for (int yBranch = 2; yBranch < height - 2; yBranch++) {
			branchCoords.addAll(generateBranches(world, yBranch, 0, 0, 0.15f, 0.25f, Math.round((height - yBranch) * 0.25f), 1, 0.25f));
		}
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 2, 2, leaf, EnumReplaceMode.NONE);
		}

		int leafSpawn = height + 1;
		float diameterchange = (float) 1 / height;
		int leafSpawned = 2;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);

		while (leafSpawn > 1) {
			generateAdjustedCylinder(world, leafSpawn--, 3 * diameterchange * leafSpawned, 1, leaf);
			generateAdjustedCylinder(world, leafSpawn--, 2 * diameterchange * leafSpawned, 1, leaf);
			leafSpawned += 2;
		}

	}

}
