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

public class WorldGenCocobolo extends WorldGenTree {

	public WorldGenCocobolo(ITreeGenData tree) {
		super(tree, 8, 8);
	}

	@Override
	public Set<BlockPos> generateTrunk(World world, Random rand, TreeBlockTypeLog wood, BlockPos startPos) {
		return WorldGenHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int leafSpawn = height;

		for (BlockPos treeTop : branchEnds) {
			WorldGenHelper.addBlock(world, treeTop.up(), leaf, WorldGenHelper.EnumReplaceMode.AIR);
		}
		leafSpawn--;
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 1 + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		if (height > 10) {
			WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn--, 0), girth, 2 + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		}
		WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(0, leafSpawn, 0), girth, girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);

		leafSpawn--;

		while (leafSpawn > 4) {
			int offset = 1;
			if (rand.nextBoolean()) {
				offset = -1;
			}

			float radius = (leafSpawn % 2 == 0) ? 2 + girth : girth;
			WorldGenHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.add(offset, leafSpawn, offset), girth, radius, 1, WorldGenHelper.EnumReplaceMode.AIR);

			leafSpawn--;
		}
	}
}
