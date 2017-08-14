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

public class WorldGenAcacia extends WorldGenTree {

	public WorldGenAcacia(ITreeGenData tree) {
		super(tree, 5, 2);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		WorldGenHelper.Direction leanDirection = WorldGenHelper.Direction.getRandom(rand);
		float leanAmount = height / 4.0f;

		Set<BlockPos> treeTops = WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, leanDirection.facing, leanAmount);
		if (height > 5 && rand.nextBoolean()) {
			WorldGenHelper.Direction branchDirection = WorldGenHelper.Direction.getRandomOther(rand, leanDirection);
			Set<BlockPos> treeTops2 = WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, Math.round(height * 0.66f), girth, 0, 0, branchDirection.facing, leanAmount);
			treeTops.addAll(treeTops2);
		}

		Set<BlockPos> branchEnds = new HashSet<>();

		for (BlockPos treeTop : treeTops) {
			int xOffset = treeTop.getX();
			int yOffset = treeTop.getY() - startPos.getY() + 1;
			int zOffset = treeTop.getZ();
			float canopyMultiplier = (1.5f * height - yOffset + 2) / 4.0f;
			int canopyThickness = Math.max(1, Math.round(yOffset / 10.0f));

			branchEnds.add(new BlockPos(xOffset, startPos.getY() + yOffset--, zOffset));
			yOffset--;

			float canopyWidth = rand.nextBoolean() ? 3.0f : 2.5f;
			int radius = Math.round(canopyMultiplier * canopyWidth - 4);
			BlockPos pos = new BlockPos(xOffset, startPos.getY() + yOffset - canopyThickness, zOffset);
			branchEnds.addAll(WorldGenHelper.generateBranches(world, rand, wood, pos, girth, 0.0f, 0.1f, radius, 2, 1.0f));
		}

		return branchEnds;
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		for (BlockPos branchEnd : branchEnds) {
			int leafSpawn = branchEnd.getY() - startPos.getY();
			int canopyThickness = Math.max(1, Math.round(leafSpawn / 10.0f));
			float canopyMultiplier = (1.5f * height - leafSpawn + 2) / 4.0f;
			float canopyWidth = rand.nextBoolean() ? 1.0f : 1.5f;
			BlockPos center = new BlockPos(branchEnd.getX(), leafSpawn - canopyThickness + 1 + startPos.getY(), branchEnd.getZ());
			float radius = Math.max(1, canopyMultiplier * canopyWidth + girth);
			WorldGenHelper.generateCylinderFromPos(world, leaf, center, radius, canopyThickness, WorldGenHelper.EnumReplaceMode.AIR);
		}
	}
}
