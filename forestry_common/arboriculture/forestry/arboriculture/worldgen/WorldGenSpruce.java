/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.worldgen;

import net.minecraft.init.Blocks;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.BlockType;

public class WorldGenSpruce extends WorldGenTree {

	public WorldGenSpruce(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);

		while (leafSpawn > 2) {
			generateAdjustedCylinder(leafSpawn--, 2, 1, leaf);
			generateAdjustedCylinder(leafSpawn--, 1, 1, leaf);
		}

	}

	@Override
	public void preGenerate() {
		height = determineHeight(5, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(Blocks.log, 1);
	}

}
