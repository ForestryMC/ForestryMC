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
package forestry.apiculture.worldgen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;

public abstract class WorldGenHive extends WorldGenerator {

	@Override
	public abstract boolean generate(World world, Random random, int x, int y, int z);

	protected void setHive(World world, int x, int y, int z, int meta) {
		boolean placed = world.setBlock(x, y, z, ForestryBlock.beehives.block(), meta, Defaults.FLAG_BLOCK_SYNCH);
		if (!placed)
			return;

		if (!ForestryBlock.beehives.isBlockEqual(world, x, y, z))
			return;

		ForestryBlock.beehives.block().onBlockAdded(world, x, y, z);
		world.markBlockForUpdate(x, y, z);

		postGen(world, x, y, z, meta);
	}

	protected void postGen(World world, int x, int y, int z, int meta) {
		ForestryBlock.beehives.block().onBlockPlaced(world, x, y, z, 0, 0.0f, 0.0f, 0.0f, meta);
	}
}
