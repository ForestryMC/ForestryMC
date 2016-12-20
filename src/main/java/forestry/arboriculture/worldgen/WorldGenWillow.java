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

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;

public class WorldGenWillow extends WorldGenTree {

	public WorldGenWillow(ITreeGenData tree) {
		super(tree, 5, 2);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0.8f, null, 0);
		WorldGenHelper.generateSupportStems(wood, world, rand, startPos, height, girth, 0.2f, 0.2f);

		int leafSpawn = height - 4;
		while (leafSpawn > 2) {
			// support branches for tall willows, keeps the leaves from decaying immediately
			if ((leafSpawn - 3) % 6 == 0) {
				WorldGenHelper.generateBranches(world, rand, wood, startPos.add(0, leafSpawn, 0), girth, 0, 0, 2, 1, 1.0f);
			}
			leafSpawn--;
		}

		return Collections.emptySet();
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int leafSpawn = height + 1;

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.5f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 2.5f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 3f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 3f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		while (leafSpawn > 2) {
			WorldGenHelper.generateCircleFromTreeStartPos(world, rand, startPos.add(0, leafSpawn--, 0), girth, 4f, 2, 1, leaf, 1.0f, WorldGenHelper.EnumReplaceMode.AIR);
		}

		WorldGenHelper.generateCircleFromTreeStartPos(world, rand, startPos.add(0, leafSpawn--, 0), girth, 4f, 1, 1, leaf, 1.0f, WorldGenHelper.EnumReplaceMode.AIR);
		WorldGenHelper.generateCircleFromTreeStartPos(world, rand, startPos.add(0, leafSpawn--, 0), girth, 4f, 1, 1, leaf, 1.0f, WorldGenHelper.EnumReplaceMode.AIR);
		WorldGenHelper.generateCircleFromTreeStartPos(world, rand, startPos.add(0, leafSpawn, 0), girth, 4f, 1, 1, leaf, 0.4f, WorldGenHelper.EnumReplaceMode.AIR);
	}
}
