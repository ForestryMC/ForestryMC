/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
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

		if (tryPlaceGroundHive(world, x, y, z, 6, Blocks.dirt, Blocks.grass)) {
			world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 0);
			return true;
		}

		return false;
	}
}
