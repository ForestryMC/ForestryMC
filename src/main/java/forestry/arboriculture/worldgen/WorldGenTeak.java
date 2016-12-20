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
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;

public class WorldGenTeak extends WorldGenTree {

	public WorldGenTeak(ITreeGenData tree) {
		super(tree, 6, 3);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);

		int branchWidth = height / 3;

		return WorldGenHelper.generateBranches(world, rand, wood, startPos.add(0, height - 3, 0), girth, 0.2f, 0.5f, branchWidth, 1, 1.0f);
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		float leafMultiplier = height / 6.0f;
		if (leafMultiplier > 2) {
			leafMultiplier = 2;
		}

		for (BlockPos branchEnd : branchEnds) {
			WorldGenHelper.generateCircle(world, rand, branchEnd, 2, Math.round(3 * leafMultiplier), 2, leaf, 1.0f, WorldGenHelper.EnumReplaceMode.AIR);
		}

		int leafSpawn = height + 1;

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 0.5f * leafMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.9f * leafMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.9f * leafMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		if (rand.nextBoolean()) {
			WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.9f * leafMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		}

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn, 0), girth, 0.5f * leafMultiplier + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
	}
}
