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

public class WorldGenMahoe extends WorldGenTree {

	public WorldGenMahoe(ITreeGenData tree) {
		super(tree, 6, 3);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		Set<BlockPos> branchCoords = new HashSet<>();

		branchCoords.addAll(WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0));

		for (int yBranch = 2; yBranch < height - 1; yBranch++) {
			branchCoords.addAll(WorldGenHelper.generateBranches(world, rand, wood, startPos.add(0, yBranch, 0), girth, 0.15f, 0.25f, Math.round((height - yBranch) * 0.75f), 1, 0.25f));
		}
		return branchCoords;
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		for (BlockPos branchEnd : branchEnds) {
			WorldGenHelper.generateCylinderFromPos(world, leaf, branchEnd, 2 + girth, 2, WorldGenHelper.EnumReplaceMode.AIR);
		}

		int yCenter = height - girth;
		yCenter = yCenter > 3 ? yCenter : 4;
		WorldGenHelper.generateSphereFromTreeStartPos(world, startPos.add(0, yCenter, 0), girth, 3 + rand.nextInt(girth), leaf, WorldGenHelper.EnumReplaceMode.AIR);
	}
}
