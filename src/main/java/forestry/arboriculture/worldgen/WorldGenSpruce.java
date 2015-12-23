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

public class WorldGenSpruce extends WorldGenTree {

	public WorldGenSpruce(ITreeGenData tree) {
		super(tree, 5, 3);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);

		int branchWidth = height / 4;
		while (leafSpawn > 2) {
			int leafRadius = Math.min(4, branchWidth);
			List<BlockPos> branchCoords = generateBranches(world, leafSpawn, 0, 0, 0.2f, 0.5f, branchWidth, 1);
			for (BlockPos branchEnd : branchCoords) {
				generateAdjustedCircle(world, branchEnd.getY(), branchEnd.getX(), branchEnd.getZ(), leafRadius, 3, 2, leaf, 1.0f, EnumReplaceMode.SOFT);
			}
			generateAdjustedCylinder(world, leafSpawn--, 2, 1, leaf);
			generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);
			branchWidth++;
		}
	}

}
