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
import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

/**
 * This is a dummy and needs to be replaced with something proper.
 */
public class FeatureJungle extends FeatureTreeVanilla {

	public FeatureJungle(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public Set<BlockPos> generateTrunk(LevelAccessor world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		int height = this.height;
		float vinesChance = 0.0f;
		if (girth >= 2) {
			height *= 1.5f;
			vinesChance = 0.8f;
		}

		FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, vinesChance, null, 0);

		Set<BlockPos> branchCoords = new HashSet<>();
		if (height > 10) {
			int branchSpawn = 6;
			while (branchSpawn < height - 2) {
				branchCoords.addAll(FeatureHelper.generateBranches(world, rand, wood, startPos.offset(0, branchSpawn, 0), girth, 0.5f, 0f, 2, 1, 0.25f));
				branchSpawn += rand.nextInt(4);
			}
		}

		return branchCoords;
	}

	@Override
	protected void generateLeaves(LevelAccessor world, Random rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		int height = this.height;
		if (girth >= 2) {
			height *= 1.5f;
		}

		for (BlockPos branchEnd : contour.getBranchEnds()) {
			FeatureHelper.generateCylinderFromPos(world, leaf, branchEnd, girth, 1, FeatureHelper.EnumReplaceMode.AIR, contour);
		}

		int leafSpawn = height + 1;
		float canopyRadiusMultiplier = height / 7.0f;

		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn--, 0), girth, girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn--, 0), girth, 0.5f * canopyRadiusMultiplier + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);

		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn--, 0), girth, 1.9f * canopyRadiusMultiplier + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafSpawn, 0), girth, 1.9f * canopyRadiusMultiplier + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
	}
}
