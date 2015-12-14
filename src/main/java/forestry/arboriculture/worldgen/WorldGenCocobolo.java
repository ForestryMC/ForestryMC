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

public class WorldGenCocobolo extends WorldGenTree {

	public WorldGenCocobolo(ITreeGenData tree) {
		super(tree, 8, 8);
	}
	
	@Override
	public void generate(World world) {
		List<ChunkCoordinates> treeTops = generateTreeTrunk(world, height, girth);

		int leafSpawn = height;

		for (ChunkCoordinates treeTop : treeTops) {
			addLeaf(world, treeTop.posX, treeTop.posY + 1, treeTop.posZ, EnumReplaceMode.NONE);
		}
		leafSpawn--;
		generateAdjustedCylinder(world, leafSpawn--, 1, 1, leaf);

		if (height > 10) {
			generateAdjustedCylinder(world, leafSpawn--, 2, 1, leaf);
		}
		generateAdjustedCylinder(world, leafSpawn, 0, 1, leaf);
		
		leafSpawn--;
		
		while (leafSpawn > 4) {
			int offset = 1;
			if (leafSpawn % 2 == 0) {
				if (world.rand.nextBoolean()) {
					offset = -1;
				}
				generateAdjustedCylinder(world, leafSpawn, offset, offset, 2, 1, leaf, EnumReplaceMode.NONE);
			} else {
				if (world.rand.nextBoolean()) {
					offset = -1;
				}
				generateAdjustedCylinder(world, leafSpawn, offset, offset, 0, 1, leaf, EnumReplaceMode.NONE);
			}
			leafSpawn--;
		}

	}

}
