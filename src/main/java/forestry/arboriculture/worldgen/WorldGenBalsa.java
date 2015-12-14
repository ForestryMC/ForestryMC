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

public class WorldGenBalsa extends WorldGenTree {

	public WorldGenBalsa(ITreeGenData tree) {
		super(tree, 6, 6);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height;
		float leafRadius = (girth - 1.0f) / 2.0f;

		addLeaf(world, 0, leafSpawn--, 0, EnumReplaceMode.NONE);
		generateAdjustedCylinder(world, leafSpawn--, leafRadius, 1, leaf);

		if (height > 10) {
			generateAdjustedCylinder(world, leafSpawn--, leafRadius, 1, leaf);
		}

		leafSpawn--;

		while (leafSpawn > 6) {
			generateAdjustedCylinder(world, leafSpawn, leafRadius, 1, leaf);
			leafSpawn--;
		}

	}

}
