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

public class WorldGenMahogany extends WorldGenTree {

	public WorldGenMahogany(ITreeGenData tree) {
		super(tree, 12, 6);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth, 0.6f);
		generateSupportStems(world, height, girth, 0.4f, 0.4f);

		List<ChunkCoordinates> branchCoords = new ArrayList<>();
		for (int yBranch = height - 4; yBranch < height - 2; yBranch++) {
			branchCoords.addAll(generateBranches(world, yBranch, 0, 0, 0.15f, 0.25f, Math.round((height - yBranch) * 0.5f), 1, 0.25f));
		}
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 2, 2, leaf, EnumReplaceMode.NONE);
		}

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 3f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 3f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);

	}

}
