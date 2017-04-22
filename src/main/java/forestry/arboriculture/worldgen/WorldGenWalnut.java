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

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenWalnut extends WorldGenTree {

	public WorldGenWalnut(ITreeGenData tree) {
		super(tree, 7, 3);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);

		Set<BlockPos> branchEnds = new HashSet<>();

		int branchHeight = height - 3;
		float branchSize = 2;
		while (branchHeight >= 3) {
			branchEnds.addAll(WorldGenHelper.generateBranches(world, rand, wood, startPos.add(0, branchHeight, 0), girth, 0.2f, 0.2f, (int) branchSize, 1, 0.5f));
			branchHeight--;
			branchSize += 0.25f;
		}

		return branchEnds;
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int leafSpawn = height + 1;

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 0.5f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.5f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 2f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		float branchSize = 2;
		while (leafSpawn >= 3) {
			int leafRadius = Math.min(4, (int) branchSize);
			for (BlockPos branchEnd : branchEnds) {
				WorldGenHelper.generateCircle(world, rand, branchEnd, leafRadius, 2, 2, leaf, 1.0f, WorldGenHelper.EnumReplaceMode.SOFT);
			}
			leafSpawn--;
			branchSize += 0.25f;
		}
	}
}
