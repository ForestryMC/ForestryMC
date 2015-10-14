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
		generateSupportStems(world, height, girth, 0.4f, 0.4f);

		int topHeight = (height / 3) + world.rand.nextInt(height / 6);

		List<ChunkCoordinates> branchCoords = new ArrayList<>();
		for (int yBranch = topHeight; yBranch < height; yBranch++) {
			int branchLength = Math.round(height - yBranch) / 2;
			if (branchLength > 4) {
				branchLength = 4;
			}
			branchCoords.addAll(generateBranches(world, yBranch, 0, 0, 0.05f, 0.25f, branchLength, 1, 0.5f));
		}
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 1.0f, 1, leaf, EnumReplaceMode.NONE);
		}

		int leafSpawn = height + 2;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);

		while (leafSpawn > topHeight) {
			generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);
		}

		generateAdjustedCylinder(world, leafSpawn, 0, 1, leaf);
	}

}
