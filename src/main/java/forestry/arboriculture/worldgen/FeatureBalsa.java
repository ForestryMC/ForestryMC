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

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureBalsa extends FeatureTree {

	public FeatureBalsa(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	protected void generateLeaves(LevelAccessor world, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		BlockPos topPos = startPos.offset(0, height + 1, 0);
		BlockPos.MutableBlockPos leafCenter = new BlockPos.MutableBlockPos();
		float leafRadius = (girth - 1.0f) / 2.0f;

		FeatureHelper.addBlock(world, leafCenter.set(topPos), leaf, FeatureHelper.EnumReplaceMode.AIR, contour);
		leafCenter.move(Direction.DOWN);
		FeatureHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
		leafCenter.move(Direction.DOWN);

		if (height > 10) {
			FeatureHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
			leafCenter.move(Direction.DOWN);
		}

		while (leafCenter.getY() > topPos.getY() - 6) {
			FeatureHelper.generateCylinderFromPos(world, leaf, leafCenter, leafRadius + girth, 1, FeatureHelper.EnumReplaceMode.SOFT, contour);
			leafCenter.move(Direction.DOWN);
		}
	}
}
