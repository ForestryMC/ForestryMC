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

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;

/**
 * This is a dummy and needs to be replaced with something proper.
 */
public class WorldGenJungle extends WorldGenTreeVanilla {

	public WorldGenJungle(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		int height = this.height;
		float vinesChance = 0.0f;
		if (girth >= 2) {
			height *= 1.5f;
			vinesChance = 0.8f;
		}

		WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, vinesChance, null, 0);

		Set<BlockPos> branchCoords = new HashSet<>();
		if (height > 10) {
			int branchSpawn = 6;
			while (branchSpawn < height - 2) {
				branchCoords.addAll(WorldGenHelper.generateBranches(world, rand, wood, startPos.add(0, branchSpawn, 0), girth, 0.5f, 0f, 2, 1, 0.25f));
				branchSpawn += rand.nextInt(4);
			}
		}

		return branchCoords;
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int height = this.height;
		if (girth >= 2) {
			height *= 1.5f;
		}

		for (BlockPos branchEnd : branchEnds) {
			WorldGenHelper.generateCylinderFromPos(world, leaf, branchEnd, girth, 1, WorldGenHelper.EnumReplaceMode.AIR);
		}

		int leafSpawn = height + 1;
		float canopyRadiusMultiplier = height / 7.0f;

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 0.5f * canopyRadiusMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.9f * canopyRadiusMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn, 0), girth, 1.9f * canopyRadiusMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
	}
}
