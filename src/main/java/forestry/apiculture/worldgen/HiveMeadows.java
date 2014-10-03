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
import net.minecraft.world.biome.BiomeGenBase;

public class HiveMeadows extends HiveGround {

	public HiveMeadows(float genChance) {
		super(ForestryBlock.beehives.block(), 2, genChance, Blocks.dirt, Blocks.grass);
	}

	@Override
	public boolean isGoodClimate(BiomeGenBase biome, EnumTemperature temperature, EnumHumidity humidity) {
		return temperature == EnumTemperature.NORMAL && humidity == EnumHumidity.NORMAL;
	}
}
