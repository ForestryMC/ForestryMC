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

/**
 * This is a dummy and needs to be replaced with something proper.
 */
public class WorldGenJungle extends WorldGenTree {

	public WorldGenJungle(ITreeGenData tree) {
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

	}

	@Override
	public BlockType getWood() {
		return new BlockType(Blocks.log, 3);
	}

}
