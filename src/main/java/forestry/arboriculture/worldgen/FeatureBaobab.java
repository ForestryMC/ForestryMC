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

public class FeatureBaobab extends FeatureTree {

	public FeatureBaobab(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	public Set<BlockPos> generateTrunk(LevelAccessor world, RandomSource rand, TreeBlockTypeLog wood, BlockPos startPos) {
		FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height - 1, girth, 0, 0, null, 0);

		if (rand.nextFloat() < 0.3f) {
			FeatureHelper.generateCylinderFromTreeStartPos(world, wood, startPos.offset(0, height - 1, 0), girth, girth, 1, FeatureHelper.EnumReplaceMode.SOFT, TreeContour.EMPTY);
		} else if (rand.nextBoolean()) {
			FeatureHelper.generateCylinderFromTreeStartPos(world, wood, startPos.offset(0, height - 1, girth / 2), girth, girth - 1, 1, FeatureHelper.EnumReplaceMode.SOFT, TreeContour.EMPTY);
		}

		return FeatureHelper.generateBranches(world, rand, wood, startPos.offset(0, height - 2, 0), girth, 0, 0.5f, 4, 6, 1.0f);
	}

	@Override
	protected void generateLeaves(LevelAccessor world, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		for (BlockPos branchEnd : contour.getBranchEnds()) {
			FeatureHelper.generateCylinderFromPos(world, leaf, branchEnd, girth, 2, FeatureHelper.EnumReplaceMode.AIR, contour);
		}

		int leafSpawn = height + 1;

		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn--, 0), girth, 2f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn--, 0), girth, 1.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn, 0), girth, 1f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);

		// Add tree top
		for (int times = 0; times < height / 2; times++) {
			int h = height - 1 + rand.nextInt(4);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}

			int x_off = -girth + rand.nextInt(2 * girth);
			int y_off = -girth + rand.nextInt(2 * girth);

			BlockPos center = startPos.offset(x_off, h, y_off);
			int radius = 1;
			if (girth > 1) {
				radius += rand.nextInt(girth - 1);
			}
			FeatureHelper.generateSphere(world, center, radius, leaf, FeatureHelper.EnumReplaceMode.AIR, contour);
		}

		// Add some smaller twigs below for flavour
		for (int times = 0; times < height / 4; times++) {
			int delim = modifyByHeight(world, 6, 0, height);
			int h = delim + (delim < height ? rand.nextInt(height - delim) : 0);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}
			int x_off = -1 + rand.nextInt(3);
			int y_off = -1 + rand.nextInt(3);

			BlockPos center = startPos.offset(x_off, h, y_off);
			int radius = 1 + rand.nextInt(2);
			FeatureHelper.generateSphere(world, center, radius, leaf, FeatureHelper.EnumReplaceMode.AIR, contour);
		}
	}
}
