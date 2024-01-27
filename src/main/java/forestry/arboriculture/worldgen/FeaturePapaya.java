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

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeaturePapaya extends FeatureTree {

	public FeaturePapaya(ITreeGenData tree) {
		super(tree, 7, 2);
	}

	@Override
	public Set<BlockPos> generateTrunk(LevelAccessor world, RandomSource rand, TreeBlockTypeLog wood, BlockPos startPos) {
		FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);
		return FeatureHelper.generateBranches(world, rand, wood, startPos.offset(0, height - 1, 0), girth, 0.15f, 0.25f, height / 4, 1, 0.25f);
	}

	@Override
	protected void generateLeaves(LevelAccessor world, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		for (BlockPos branchEnd : contour.getBranchEnds()) {
			FeatureHelper.generateCylinderFromPos(world, leaf, branchEnd, 1 + girth, 1, FeatureHelper.EnumReplaceMode.AIR, contour);
		}

		int yCenter = height - girth;
		yCenter = yCenter > 3 ? yCenter : 4;
		FeatureHelper.generateSphereFromTreeStartPos(world, startPos.offset(0, yCenter, 0), girth, Math.round((2 + rand.nextInt(girth)) * (height / 8.0f)), leaf, FeatureHelper.EnumReplaceMode.AIR, contour);
	}
}
