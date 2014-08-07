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
import forestry.core.config.ForestryBlock;
import forestry.core.worldgen.BlockType;

public class WorldGenGiganteum extends WorldGenSequoia {

	public WorldGenGiganteum(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		super.generate();
		//generateSupportStems(height, girth, 0.8f, 0.6f);
	}

	@Override
	public void preGenerate() {
		height = determineHeight(35, 15);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	@Override
	public BlockType getWood() {
		return new BlockType(ForestryBlock.log7, 0);
	}

}
