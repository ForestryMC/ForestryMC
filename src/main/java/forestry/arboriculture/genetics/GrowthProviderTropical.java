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

import forestry.core.utils.StringUtil;
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
		return StringUtil.localize("gui.growth.tropical");
	}

}
