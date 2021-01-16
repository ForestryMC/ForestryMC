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
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureGreenheart extends FeatureTree {
	public FeatureGreenheart(ITreeGenData tree) {
		super(tree, 10, 8);
	}

	@Override
	public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		return place(world, rand, pos, false);
	}

	@Override
	public Set<BlockPos> generateTrunk(IWorld world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0.4f, null, 0);
		FeatureHelper.generateSupportStems(wood, world, rand, startPos, height, girth, 0.5f, 0.2f);
		return Collections.emptySet();
	}

	@Override
	protected void generateLeaves(IWorld world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int leafSpawn = height + 1;

		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 0.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);

		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);

		while (leafSpawn > height - 4) {
			FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.9f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
		}
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn, 0), girth, 0.5f + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);

		if (height > 10) {
			// Add some smaller twigs below for flavour
			for (int times = 0; times < height / 4; times++) {
				int h = 10 + rand.nextInt(height - 10);
				if (rand.nextBoolean() && h < height / 2) {
					h = height / 2 + rand.nextInt(height / 2);
				}
				int x_off = -1 + rand.nextInt(3);
				int y_off = -1 + rand.nextInt(3);
				FeatureHelper.generateSphere(world, startPos.add(x_off, h, y_off), 1 + rand.nextInt(1), leaf, FeatureHelper.EnumReplaceMode.AIR);
			}
		}
	}
}
