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

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenWalnut extends WorldGenTree {

	public WorldGenWalnut(ITreeGenData tree) {
		super(tree, 7, 3);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(world, leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);

		float branchSize = 2;
		while (leafSpawn >= 3) {
			int leafRadius = Math.min(4, (int) branchSize);
			List<BlockPos> branchCoords = generateBranches(world, leafSpawn, 0, 0, 0.2f, 0.2f, (int) branchSize, 1, 0.5f);
			for (BlockPos branchEnd : branchCoords) {
				generateAdjustedCircle(world, branchEnd, leafRadius, 2, 2, leaf, 1.0f, EnumReplaceMode.SOFT);
			}
			leafSpawn--;
			branchSize += 0.25f;
		}

		if (hasPods()) {
			generatePods(world, height, girth);
		}
	}

}
