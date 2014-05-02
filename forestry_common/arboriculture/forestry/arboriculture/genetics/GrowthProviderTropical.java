/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import java.util.EnumSet;

import net.minecraft.world.World;

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.ITreeGenome;

public class GrowthProviderTropical extends GrowthProvider {

	@Override
	public EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, int xPos, int yPos, int zPos) {
		EnumGrowthConditions light = getConditionFromLight(world, xPos, yPos, zPos);
		EnumGrowthConditions moisture = getConditionsFromRainfall(world, xPos, yPos, zPos, 0.9f, 2.0f);
		EnumGrowthConditions temperature = getConditionsFromTemperature(world, xPos, yPos, zPos, 1.2f, 1.9f);

		EnumSet<EnumGrowthConditions> conditions = EnumSet.of(light, moisture, temperature);

		EnumGrowthConditions result = EnumGrowthConditions.HOSTILE;
		for (EnumGrowthConditions cond : conditions) {
			if (cond == EnumGrowthConditions.HOSTILE)
				return EnumGrowthConditions.HOSTILE;

			if (cond.ordinal() > result.ordinal())
				result = cond;
		}

		return result;
	}

	@Override
	public String getDescription() {
		return "Tropical";
	}

}
