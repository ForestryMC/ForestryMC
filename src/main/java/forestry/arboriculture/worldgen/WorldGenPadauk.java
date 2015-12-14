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

import java.util.Random;

import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.world.ITreeGenData;

public class WorldGenPadauk extends WorldGenTree {

	public WorldGenPadauk(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 1.5f, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, 3f, 1, leaf);
		
		int count = 0;
		int max = 3;
		int min = 1;
		int canopyHeight = world.rand.nextInt(max - min + 1) + min;
		
		while (leafSpawn > 3 && count < canopyHeight) {
			generateAdjustedCylinder(world, leafSpawn--, 4.5f, 1, leaf);
			count++;
			//Random Trunk Branches
			for (int i = 0; i < girth * 4; i++) {
				if (world.rand.nextBoolean()) {
					
					int[] offset = {-1, 1};
					int offsetValue = (offset[new Random().nextInt(offset.length)]);
					int maxBranchLength = 3;
					int branchLength = new Random().nextInt(maxBranchLength + 1);
					ForgeDirection[] direction = {ForgeDirection.NORTH, ForgeDirection.EAST};
					ForgeDirection directionValue = (direction[new Random().nextInt(direction.length)]);
					int branchSpawn = leafSpawn;

					for (int j = 1; j < branchLength + 1; j++) {
						if (j == branchLength && world.rand.nextBoolean()) { //Just adding a bit of variation to the ends for character
							branchSpawn += 1;
						}

						wood.setDirection(directionValue);
						if (directionValue == ForgeDirection.NORTH) {
							addWood(world, 0, branchSpawn, j * offsetValue, EnumReplaceMode.ALL);
						} else if (directionValue == ForgeDirection.EAST) {
							addWood(world, j * offsetValue, branchSpawn, 0, EnumReplaceMode.ALL);
						}
					}
				}
			}
		}
	}

}
