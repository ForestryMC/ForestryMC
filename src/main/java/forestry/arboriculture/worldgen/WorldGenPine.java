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

public class WorldGenPine extends WorldGenTree {

	public WorldGenPine(ITreeGenData tree) {
		super(tree, 6, 4);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);

		Set<BlockPos> branchEnds = new HashSet<>();
		for (int yBranch = 2; yBranch < height - 2; yBranch++) {
			branchEnds.addAll(WorldGenHelper.generateBranches(world, rand, wood, startPos.add(0, yBranch, 0), girth, 0.05f, 0.1f, Math.round((height - yBranch) * 0.25f), 1, 0.25f));
		}
		return branchEnds;
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		for (BlockPos branchEnd : branchEnds) {
			WorldGenHelper.generateCylinderFromPos(world, leaf, branchEnd, 2 + girth, 1, WorldGenHelper.EnumReplaceMode.AIR);
		}

		int leafSpawn = height + 1;
		float diameterchange = 1.25f / height;
		int leafSpawned = 2;

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, (float) 1 + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		while (leafSpawn > 1) {
			WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 3 * diameterchange * leafSpawned + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
			WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 2 * diameterchange * leafSpawned + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
			leafSpawned += 2;
		}
	}
}
