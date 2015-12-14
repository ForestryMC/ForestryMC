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

public class WorldGenWenge extends WorldGenTree {

	public WorldGenWenge(ITreeGenData tree) {
		super(tree, 6, 2);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);

		float branchSize = 3;
		while (leafSpawn > 1) {
			generateAdjustedCylinder(world, leafSpawn--, 3f, 1, leaf);

			List<ChunkCoordinates> branchCoords = generateBranches(world, leafSpawn, 0, 0, 0.2f, 0.2f, (int) branchSize, 2, 0.75f);
			for (ChunkCoordinates branchEnd : branchCoords) {
				generateAdjustedCircle(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 3, 3, 2, leaf, 1.0f, EnumReplaceMode.SOFT);
			}
			leafSpawn--;
			branchSize += 0.5f;
		}
	}

}
