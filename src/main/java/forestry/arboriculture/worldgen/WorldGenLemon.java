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

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.WorldGenHelper;

public class WorldGenLemon extends WorldGenTree {

	public WorldGenLemon(ITreeGenData tree) {
		super(tree, 6, 3);
	}

	@Override
	protected void generateLeaves(World world, Random rand, TreeBlockTypeLeaf leaf, List<BlockPos> branchEnds, BlockPos startPos) {
		int yCenter = height - girth;
		yCenter = yCenter > 2 ? yCenter : 3;
		int radius = Math.round((2 + rand.nextInt(girth)) * (height / 4.0f));
		if (radius > 4) {
			radius = 4;
		}
		WorldGenHelper.generateSphereFromTreeStartPos(world, startPos.add(0, yCenter, 0), girth, radius, leaf, WorldGenHelper.EnumReplaceMode.AIR);
	}
}
