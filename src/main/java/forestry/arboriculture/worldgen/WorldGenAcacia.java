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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenAcacia extends WorldGenTree {

	public WorldGenAcacia(ITreeGenData tree) {
		super(tree, 5, 2);
	}

	@Override
	public void generate(World world) {
		Direction leanDirection = Direction.getRandom(world.rand);
		float leanAmount = height / 4.0f;

		List<ChunkCoordinates> treeTops = generateTreeTrunk(world, height, girth, 0, leanDirection.forgeDirection, leanAmount);
		if (height > 5 && world.rand.nextBoolean()) {
			Direction branchDirection = Direction.getRandomOther(world.rand, leanDirection);
			List<ChunkCoordinates> treeTops2 = generateTreeTrunk(world, Math.round(height * 0.66f), girth, 0, branchDirection.forgeDirection, leanAmount);
			treeTops.addAll(treeTops2);
		}

		List<ChunkCoordinates> branchLocations = new ArrayList<>();

		for (ChunkCoordinates treeTop : treeTops) {
			int xOffset = treeTop.posX;
			int yOffset = treeTop.posY + 1;
			int zOffset = treeTop.posZ;
			float canopyMultiplier = (1.5f * height - yOffset + 2) / 4.0f;
			int canopyThickness = Math.max(1, Math.round(yOffset / 10.0f));

			generateAdjustedCylinder(world, yOffset--, xOffset, zOffset, canopyMultiplier, 1, leaf, EnumReplaceMode.NONE);

			float canopyWidth = world.rand.nextBoolean() ? 3.0f : 2.5f;
			List<ChunkCoordinates> branches = generateBranches(world, yOffset - canopyThickness, xOffset, zOffset, 0.0f, 0.1f, Math.round(canopyMultiplier * canopyWidth - 4), 2);
			branchLocations.addAll(branches);
		}

		for (ChunkCoordinates branchLocation : branchLocations) {
			int leafSpawn = branchLocation.posY;
			int canopyThickness = Math.max(1, Math.round(leafSpawn / 10.0f));
			float canopyMultiplier = (1.5f * height - leafSpawn + 2) / 4.0f;
			float canopyWidth = world.rand.nextBoolean() ? 1.0f : 1.5f;
			generateAdjustedCylinder(world, leafSpawn - canopyThickness + 1, branchLocation.posX, branchLocation.posZ, canopyMultiplier * canopyWidth, canopyThickness, leaf, EnumReplaceMode.NONE);
		}
	}

}
