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

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureBalsa extends FeatureTree {

	public FeatureBalsa(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	protected void generateLeaves(IWorld world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		BlockPos topPos = startPos.add(0, height + 1, 0);
		BlockPos.MutableBlockPos leafCenter = new BlockPos.MutableBlockPos(topPos);
		float leafRadius = (girth - 1.0f) / 2.0f;

		FeatureHelper.addBlock(world, leafCenter, leaf, FeatureHelper.EnumReplaceMode.AIR);
		leafCenter.move(Direction.DOWN);
		FeatureHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
		leafCenter.move(Direction.DOWN);

		if (height > 10) {
			FeatureHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
			leafCenter.move(Direction.DOWN);
		}

		while (leafCenter.getY() > topPos.getY() - 6) {
			FeatureHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, FeatureHelper.EnumReplaceMode.SOFT);
			leafCenter.move(Direction.DOWN);
		}
	}
}
