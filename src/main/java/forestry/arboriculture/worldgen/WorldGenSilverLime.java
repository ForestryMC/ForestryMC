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

public class WorldGenSilverLime extends WorldGenTree {

	public WorldGenSilverLime(ITreeGenData tree) {
		super(tree, 6, 4);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);
		List<BlockPos> branchCoords = generateBranches(world, 3 + world.rand.nextInt(1), 0, 0, 0.25f, 0.10f, Math.round(height * 0.25f), 2, 0.5f);
		for (BlockPos branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd, 0, 1, leaf, EnumReplaceMode.AIR);
		}

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		float radius = 1;
		while (leafSpawn > 1) {
			generateAdjustedCylinder(world, leafSpawn--, radius, 1, leaf);
			radius += 0.25;
		}

		if (hasPods()) {
			generatePods(world, height, girth);
		}
	}

}
