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

import forestry.api.apiculture.hives.HiveTree;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.config.ForestryBlock;
import net.minecraft.world.biome.BiomeGenBase;

public class HiveForest extends HiveTree {

	public HiveForest(float genChance) {
		super(ForestryBlock.beehives.block(), 1, genChance);
	}

	@Override
	public boolean isGoodHumidity(EnumHumidity humidity) {
		return humidity == EnumHumidity.NORMAL;
	}

	@Override
	public boolean isGoodTemperature(EnumTemperature temperature) {
		return temperature == EnumTemperature.NORMAL;
	}
}
