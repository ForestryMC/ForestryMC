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

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenChestnut extends WorldGenTree {

	public WorldGenChestnut(ITreeGenData tree) {
		super(tree, 7, 3);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f, 1, leaf);

		while (leafSpawn > 4) {
			generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);
		}
		if (world.rand.nextBoolean()) {
			generateAdjustedCylinder(world, leafSpawn--, 2f, 1, leaf);
		}

		int branchRadius = height / 2;

		List<BlockPos> branchCoords = generateBranches(world, leafSpawn, 0, 0, 0.5f, 0.5f, branchRadius, 2);
		for (BlockPos branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd, 1.0f, 2, leaf, EnumReplaceMode.NONE);
		}

		generateAdjustedCylinder(world, leafSpawn, 1.5f, 1, leaf);

	}

}
