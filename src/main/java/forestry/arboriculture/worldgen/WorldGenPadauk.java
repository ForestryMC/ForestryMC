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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;

public class WorldGenPadauk extends WorldGenTree {

	public WorldGenPadauk(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);

		int branchSpawn = height - 2;

		int count = 0;
		int max = 3;
		int min = 1;
		int canopyHeight = rand.nextInt(max - min + 1) + min;

		while (branchSpawn > 3 && count < canopyHeight) {
			count++;
			//Random Trunk Branches
			for (int i = 0; i < girth * 4; i++) {
				if (rand.nextBoolean()) {

					int[] offset = {-1, 1};
					int offsetValue = offset[new Random().nextInt(offset.length)];
					int maxBranchLength = 3;
					int branchLength = new Random().nextInt(maxBranchLength + 1);
					EnumFacing[] direction = {EnumFacing.NORTH, EnumFacing.EAST};
					EnumFacing directionValue = direction[new Random().nextInt(direction.length)];
					int branchSpawnY = branchSpawn;

					for (int j = 1; j < branchLength + 1; j++) {
						if (j == branchLength && rand.nextBoolean()) { //Just adding a bit of variation to the ends for character
							branchSpawnY += 1;
						}

						wood.setDirection(directionValue);
						if (directionValue == EnumFacing.NORTH) {
							WorldGenHelper.addBlock(world, startPos.add(0, branchSpawnY, j * offsetValue), wood, WorldGenHelper.EnumReplaceMode.ALL);
						} else if (directionValue == EnumFacing.EAST) {
							WorldGenHelper.addBlock(world, startPos.add(j * offsetValue, branchSpawnY, 0), wood, WorldGenHelper.EnumReplaceMode.ALL);
						}
					}
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int leafSpawn = height + 1;

		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1.5f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 3f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		int count = 0;
		int max = 3;
		int min = 1;
		int canopyHeight = rand.nextInt(max - min + 1) + min;

		while (leafSpawn > 3 && count < canopyHeight) {
			int yCenter = leafSpawn--;
			WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, yCenter, 0), girth, 4.5f + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
			count++;
		}
	}
}
