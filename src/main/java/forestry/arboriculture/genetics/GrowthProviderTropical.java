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
package forestry.arboriculture.genetics;

import java.util.EnumSet;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.ITreeGenome;
import forestry.core.utils.Translator;

public class GrowthProviderTropical extends GrowthProvider {

	@Override
	public EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, BlockPos pos) {
		EnumGrowthConditions light = getConditionFromLight(world, pos);
		EnumGrowthConditions moisture = getConditionsFromRainfall(world, pos, Biomes.JUNGLE.getRainfall(), 2.0f);

		float jungleTemperature = Biomes.JUNGLE.getFloatTemperature(pos);
		float desertTemperature = Biomes.DESERT.getFloatTemperature(pos);
		EnumGrowthConditions temperature = getConditionsFromTemperature(world, pos, jungleTemperature, desertTemperature - 0.1f);

		EnumSet<EnumGrowthConditions> conditions = EnumSet.of(light, moisture, temperature);

		EnumGrowthConditions result = EnumGrowthConditions.HOSTILE;
		for (EnumGrowthConditions cond : conditions) {
			if (cond == EnumGrowthConditions.HOSTILE) {
				return EnumGrowthConditions.HOSTILE;
			}

			if (cond.ordinal() > result.ordinal()) {
				result = cond;
			}
		}

		return result;
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocal("for.growth.tropical");
	}

}
