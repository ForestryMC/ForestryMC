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

import forestry.api.apiculture.hives.HiveGround;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.ForestryBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class HiveSnow extends HiveGround {

	public HiveSnow(float genChance) {
		super(ForestryBlock.beehives.block(), 6, genChance, Blocks.dirt, Blocks.grass, Blocks.snow);
	}

	@Override
	public boolean isGoodClimate(BiomeGenBase biome, EnumTemperature temperature, EnumHumidity humidity) {
		return temperature == EnumTemperature.COLD || temperature == EnumTemperature.ICY;
	}

	@Override
	public void postGen(World world, int x, int y, int z) {
		if (world.isAirBlock(x, y + 1, z))
			world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 0);
	}
}
