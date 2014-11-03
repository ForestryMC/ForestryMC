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

public class WorldGenMahoe extends WorldGenTree {

	public WorldGenMahoe(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int yCenter = height - girth;
		yCenter = yCenter > 3 ? yCenter : 4;
		generateSphere(getCenteredAt(yCenter, 0), 3 + rand.nextInt(girth), leaf, EnumReplaceMode.NONE);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(6, 3);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

}
