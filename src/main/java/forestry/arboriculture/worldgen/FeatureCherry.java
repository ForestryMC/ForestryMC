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
import net.minecraft.world.IWorld;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureCherry extends FeatureTree {

	public FeatureCherry(ITreeGenData tree) {
		super(tree, 4, 4);
	}

	@Override
	public Set<BlockPos> generateTrunk(IWorld world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);

		Set<BlockPos> branchCoords = new HashSet<>();

		int branchHeight = height - 1;
		int branchWidth = height / 2;
		while (branchHeight > 2) {
			branchCoords.addAll(FeatureHelper.generateBranches(world, rand, wood, startPos.add(0, branchHeight, 0), girth, 0.2f, 0.5f, branchWidth, 1, 1.0f));
			branchHeight -= 2;
			branchWidth++;
		}
		return branchCoords;
	}

	@Override
	protected void generateLeaves(IWorld world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int leafSpawn = height + 2;
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn, 0), girth, 1 + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);

		for (BlockPos branchEnd : branchEnds) {
			FeatureHelper.generateCircle(world, rand, branchEnd.up(), 3, 3, 1, leaf, 1.0f, FeatureHelper.EnumReplaceMode.AIR);
			FeatureHelper.generateCircle(world, rand, branchEnd, 4, 3, 1, leaf, 1.0f, FeatureHelper.EnumReplaceMode.AIR);
		}
	}
}
