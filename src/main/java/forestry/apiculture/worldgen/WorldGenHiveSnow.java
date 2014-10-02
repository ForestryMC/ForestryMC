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

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.EnumTemperature;

public class WorldGenHiveSnow extends WorldGenHive {

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		if (EnumTemperature.getFromValue(biome.temperature) != EnumTemperature.ICY
				&& EnumTemperature.getFromValue(biome.temperature) != EnumTemperature.COLD)
			return false;

		return tryPlaceGroundHive(world, x, z, 6, Blocks.dirt, Blocks.grass, Blocks.snow);
	}

	@Override
	protected void postGen(World world, int x, int y, int z, int meta) {
		super.postGen(world, x, y, z, meta);
		if (world.isAirBlock(x, y + 1, z))
			world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 0);
	}
}
