/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.worldgen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public class WorldGenHiveJungle extends WorldGenHive {

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		if (EnumTemperature.getFromValue(biome.temperature) != EnumTemperature.WARM
				|| EnumHumidity.getFromValue(biome.rainfall) != EnumHumidity.DAMP)
			return false;

		return tryPlaceTreeHive(world, x, y, z, 4);
	}
}
