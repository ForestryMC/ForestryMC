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

/**
 * This is a dummy and needs to be replaced with something proper.
 */
public class WorldGenJungle extends WorldGenTreeVanilla {

	public WorldGenJungle(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate(World world) {
		float vinesChance = 0.0f;
		if (girth >= 2) {
			height *= 1.5f;
			vinesChance = 0.8f;
		}

		generateTreeTrunk(world, height, girth, vinesChance);

		if (height > 10) {
			List<ChunkCoordinates> branchCoords = new ArrayList<>();
			int branchSpawn = 6;
			while (branchSpawn < height - 2) {
				branchCoords.addAll(generateBranches(world, branchSpawn, 0, 0, 0.5f, 0f, 2, 1, 0.25f));
				branchSpawn += world.rand.nextInt(4);
			}

			for (ChunkCoordinates branchEnd : branchCoords) {
				generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 0f, 1, leaf, EnumReplaceMode.NONE);
			}
		}

		int leafSpawn = height + 1;
		float canopyRadiusMultiplier = height / 7.0f;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f * canopyRadiusMultiplier, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.9f * canopyRadiusMultiplier, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn, 1.9f * canopyRadiusMultiplier, 1, leaf);

	}

}
