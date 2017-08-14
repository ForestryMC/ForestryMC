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

public class WorldGenEbony extends WorldGenTree {

	public WorldGenEbony(ITreeGenData tree) {
		super(tree, 10, 4);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		int trunksGenerated = 0;

		for (int x = 0; x < girth; x++) {
			for (int z = 0; z < girth; z++) {
				if (rand.nextFloat() < 0.6f) {
					for (int y = 0; y < height; y++) {
						WorldGenHelper.addBlock(world, startPos.add(x, y, z), wood, WorldGenHelper.EnumReplaceMode.ALL);
						if (y > height / 2 && rand.nextFloat() < 0.1f * (10 / height)) {
							break;
						}
					}
					trunksGenerated++;
				} else {
					for (int i = 0; i < 1; i++) {
						world.setBlockToAir(new BlockPos(x, i, z));
					}
				}
			}
		}

		// Generate backup trunk, if we failed to generate any.
		if (trunksGenerated <= 0) {
			WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, 1, 0, 0.6f, null, 0);
		}

		return Collections.emptySet();
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		for (int times = 0; times < 2 * height; times++) {
			int h = 2 * girth + rand.nextInt(height - girth);
			if (rand.nextBoolean() && h < height / 2) {
				h = height / 2 + rand.nextInt(height / 2);
			}

			int x_off = rand.nextInt(girth);
			int y_off = rand.nextInt(girth);

			BlockPos center = startPos.add(x_off, h, y_off);
			int radius = 1 + rand.nextInt(girth);
			WorldGenHelper.generateSphere(world, center, radius, leaf, WorldGenHelper.EnumReplaceMode.AIR);
		}
	}
}
