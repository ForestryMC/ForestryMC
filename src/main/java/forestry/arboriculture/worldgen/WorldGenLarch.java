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

import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;

public class WorldGenLarch extends WorldGenTree {

	public WorldGenLarch(ITreeGenData tree) {
		super(tree, 6, 5);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		float sizeMultiplier = Math.max(height / 8, 1.0f);

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf);
		generateAdjustedCylinder(world, leafSpawn--, sizeMultiplier, 1, leaf);

		while (leafSpawn > 2) {
			generateAdjustedCylinder(world, leafSpawn--, 2 * sizeMultiplier, 1, leaf);
			generateAdjustedCylinder(world, leafSpawn--, sizeMultiplier, 1, leaf);
		}

	}

}
