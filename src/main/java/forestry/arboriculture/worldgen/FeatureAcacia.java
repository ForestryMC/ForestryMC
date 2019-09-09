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

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureAcacia extends FeatureTree {

	public FeatureAcacia(ITreeGenData tree) {
		super(tree, 5, 2);
	}

	@Override
	public Set<BlockPos> generateTrunk(IWorld world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		Direction leanDirection = FeatureHelper.DirectionHelper.getRandom(rand);
		float leanAmount = height / 4.0f;

		Set<BlockPos> treeTops = FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, leanDirection, leanAmount);
		if (height > 5 && rand.nextBoolean()) {
			Direction branchDirection = FeatureHelper.DirectionHelper.getRandomOther(rand, leanDirection);
			Set<BlockPos> treeTops2 = FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, Math.round(height * 0.66f), girth, 0, 0, branchDirection, leanAmount);
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
			branchEnds.addAll(FeatureHelper.generateBranches(world, rand, wood, pos, girth, 0.0f, 0.1f, radius, 2, 1.0f));
		}

		return branchEnds;
	}

	@Override
	protected void generateLeaves(IWorld world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		for (BlockPos branchEnd : branchEnds) {
			int leafSpawn = branchEnd.getY() - startPos.getY();
			int canopyThickness = Math.max(1, Math.round(leafSpawn / 10.0f));
			float canopyMultiplier = (1.5f * height - leafSpawn + 2) / 4.0f;
			float canopyWidth = rand.nextBoolean() ? 1.0f : 1.5f;
			BlockPos center = new BlockPos(branchEnd.getX(), leafSpawn - canopyThickness + 1 + startPos.getY(), branchEnd.getZ());
			float radius = Math.max(1, canopyMultiplier * canopyWidth + girth);
			FeatureHelper.generateCylinderFromPos(world, leaf, center, radius, canopyThickness, FeatureHelper.EnumReplaceMode.AIR);
		}
	}
}
