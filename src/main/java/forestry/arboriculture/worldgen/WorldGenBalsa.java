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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;

public class WorldGenBalsa extends WorldGenTree {

	public WorldGenBalsa(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		BlockPos topPos = startPos.add(0, height + 1, 0);
		BlockPos.MutableBlockPos leafCenter = new BlockPos.MutableBlockPos(topPos);
		float leafRadius = (girth - 1.0f) / 2.0f;

		WorldGenHelper.addBlock(world, leafCenter, leaf, WorldGenHelper.EnumReplaceMode.AIR);
		leafCenter.move(EnumFacing.DOWN);
		WorldGenHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
		leafCenter.move(EnumFacing.DOWN);

		if (height > 10) {
			WorldGenHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
			leafCenter.move(EnumFacing.DOWN);
		}

		while (leafCenter.getY() > topPos.getY() - 6) {
			WorldGenHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, WorldGenHelper.EnumReplaceMode.SOFT);
			leafCenter.move(EnumFacing.DOWN);
		}
	}
}
