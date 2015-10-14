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

public class WorldGenTeak extends WorldGenTree {

	public WorldGenTeak(ITreeGenData tree) {
		super(tree, 6, 3);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		float leafMultiplier = (height / 6.0f);
		if (leafMultiplier > 2) {
			leafMultiplier = 2;
		}
		int branchWidth = height / 3;
		List<ChunkCoordinates> branchCoords = generateBranches(world, height - 3, 0, 0, 0.2f, 0.5f, branchWidth, 1);
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCircle(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 2, Math.round(3 * leafMultiplier), 2, leaf, 1.0f, EnumReplaceMode.NONE);
		}

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f * leafMultiplier, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.9f * leafMultiplier, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1.9f * leafMultiplier, 1, leaf);
		if (world.rand.nextBoolean()) {
			generateAdjustedCylinder(world, leafSpawn--, 1.9f * leafMultiplier, 1, leaf);
		}

		generateAdjustedCylinder(world, leafSpawn, 0.5f * leafMultiplier, 1, leaf);

	}

}
