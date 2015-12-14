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

public class WorldGenIpe extends WorldGenTree {

	public WorldGenIpe(ITreeGenData tree) {
		super(tree, 6, 4);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;
		float adjustedGirth = girth * .65f;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.2f * adjustedGirth, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.2f * adjustedGirth, 1, leaf);

		List<ChunkCoordinates> branchCoords = new ArrayList<>();
		while (leafSpawn > 2) {
			int radius = Math.round(adjustedGirth * (height - leafSpawn) / 1.5f);
			branchCoords.addAll(generateBranches(world, leafSpawn, 0, 0, 0.25f, 0.25f, radius, 2));
			leafSpawn -= 2;
		}

		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 2.0f, 2, leaf, EnumReplaceMode.NONE);
		}
	}

}
