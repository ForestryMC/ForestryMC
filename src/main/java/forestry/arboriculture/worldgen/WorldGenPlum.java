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

public class WorldGenPlum extends WorldGenTree {

	public WorldGenPlum(ITreeGenData tree) {
		super(tree, 6, 3);
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int yCenter = height - girth;
		yCenter = yCenter > 2 ? yCenter : 3;

		Vector center = getCenteredAt(yCenter, 0, 0);
		int radius = Math.round((2 + world.rand.nextInt(girth)) * (height / 4.0f));
		if (radius > 4) {
			radius = 4;
		}
		generateSphere(world, center, radius, leaf, EnumReplaceMode.NONE);
	}

}
