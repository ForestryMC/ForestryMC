/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

		if (tryPlaceGroundHive(world, x, y, z, 6, Blocks.dirt, Blocks.grass)) {
			world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 0);
			return true;
		}

		return false;
	}
}
