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

public class WorldGenPoplar extends WorldGenTree {

	public WorldGenPoplar(ITreeGenData tree) {
		super(tree, 8, 3);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;
		float leafRadius = (girth - 1.0f) / 2.0f;

		while (leafSpawn > girth - 1) {
			generateAdjustedCylinder(world, leafSpawn--, leafRadius, 1, leaf);
		}

	}

}
