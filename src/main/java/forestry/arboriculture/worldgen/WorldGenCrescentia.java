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

import forestry.api.world.ITreeGenData;

public class WorldGenCrescentia extends WorldGenTree {

	public WorldGenCrescentia(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);
		if (rand.nextBoolean())
			generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf);

		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf);

	}

	@Override
	public void generateTreeTrunk(int height, int girth) {
		
		int offset = (girth - 1) / 2;
		
		
		//Trunk generation
		for (int x = 0; x < girth; x++) //offsets trunk in x direction
			for (int z = 0; z < girth; z++) //offsets trunk in z direction
				if (x==0 || x==girth-1) { //checks for edges in x direction
					if (z==0 || z==girth-1) {//checks for corners
						if (x==0 && z==0) {}//xz corner
						if (x==girth-1 && z==0) {}//x-1z corner
						if (x==0 && z==girth-1) {}//xz-1 corner
						if (x==girth-1 && z==0) {} //x-1z-1 corner
					} else { //these are the z only edges
						if (x==0) {
							
						} else if (x==girth-1) { 
							
						}
					}
				} else if (z==0 || z==girth-1) { //these are the x only edges
					if (z==0) {
						
					} else if (z==girth-1) {
						
					}
				} else { //these are center pieces
					for (int i = 0; i < height; i++) { //grows trunk straight up in y direction to the correct height
						addWood(x - offset, i, z - offset, EnumReplaceMode.ALL);
					}
				}
				
		//End trunk generation
		
		//Spawn pods
		for (int y = minPodHeight; y < height; y++)
			for (int x = 0; x < girth; x++)
				for (int z = 0; z < girth; z++) {

					if ((x > 0 && x < girth) && (z > 0 && z < girth))
						continue;

					tree.trySpawnFruitBlock(world, startX + x + 1, startY + y, startZ + z);
					tree.trySpawnFruitBlock(world, startX + x - 1, startY + y, startZ + z);
					tree.trySpawnFruitBlock(world, startX + x, startY + y, startZ + z + 1);
					tree.trySpawnFruitBlock(world, startX + x, startY + y, startZ + z - 1);
				}

		
	}
	
	@Override
	public void preGenerate() {
		height = determineHeight(6, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}
}
