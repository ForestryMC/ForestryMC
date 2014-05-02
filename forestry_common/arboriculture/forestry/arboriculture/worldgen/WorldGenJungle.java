/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
