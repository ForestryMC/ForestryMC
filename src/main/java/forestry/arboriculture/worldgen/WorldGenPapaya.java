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

public class WorldGenPapaya extends WorldGenTree {

	public WorldGenPapaya(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);
		
		int yCenter = height - girth;
		yCenter = yCenter > 3 ? yCenter : 4;
		generateSphere(getCenteredAt(yCenter, 0), 2 + rand.nextInt(girth), leaf, EnumReplaceMode.NONE);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(7, 2);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

}
