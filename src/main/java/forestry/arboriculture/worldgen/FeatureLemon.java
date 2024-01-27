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

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public class FeatureLemon extends FeatureTree {

	public FeatureLemon(ITreeGenData tree) {
		super(tree, 6, 3);
	}

	@Override
	protected void generateLeaves(LevelAccessor world, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		int yCenter = height - girth;
		yCenter = yCenter > 2 ? yCenter : 3;
		int radius = Math.round((2 + rand.nextInt(girth)) * (height / 4.0f));
		if (radius > 4) {
			radius = 4;
		}
		FeatureHelper.generateSphereFromTreeStartPos(world, startPos.offset(0, yCenter, 0), girth, radius, leaf, FeatureHelper.EnumReplaceMode.AIR, contour);
	}
}
