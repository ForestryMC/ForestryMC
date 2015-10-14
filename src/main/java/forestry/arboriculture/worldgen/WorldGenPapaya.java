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

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenPapaya extends WorldGenTree {

	public WorldGenPapaya(ITreeGenData tree) {
		super(tree, 7, 2);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		List<ChunkCoordinates> branchCoords = generateBranches(world, height, 0, 0, 0.15f, 0.25f, height / 4, 1, 0.25f);
		for (ChunkCoordinates branchEnd : branchCoords) {
			generateAdjustedCylinder(world, branchEnd.posY, branchEnd.posX, branchEnd.posZ, 1, 1, leaf, EnumReplaceMode.NONE);
		}

		int yCenter = height - girth;
		yCenter = yCenter > 3 ? yCenter : 4;
		generateSphere(world, getCenteredAt(yCenter, 0, 0), Math.round((2 + world.rand.nextInt(girth)) * (height / 8.0f)), leaf, EnumReplaceMode.NONE);
	}

}
