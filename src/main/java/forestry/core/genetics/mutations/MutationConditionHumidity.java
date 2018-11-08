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
package forestry.core.genetics.mutations;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.climate.IClimateProvider;
import forestry.api.core.EnumHumidity;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.Translator;

public class MutationConditionHumidity implements IMutationCondition {
	private final EnumHumidity minHumidity;
	private final EnumHumidity maxHumidity;

	public MutationConditionHumidity(EnumHumidity minHumidity, EnumHumidity maxHumidity) {
		this.minHumidity = minHumidity;
		this.maxHumidity = maxHumidity;
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		EnumHumidity biomeHumidity = climate.getHumidity();

		if (biomeHumidity.ordinal() < minHumidity.ordinal() || biomeHumidity.ordinal() > maxHumidity.ordinal()) {
			return 0;
		}
		return 1;
	}

	@Override
	public String getDescription() {
		String minHumidityString = AlleleManager.climateHelper.toDisplay(minHumidity);

		if (minHumidity != maxHumidity) {
			String maxHumidityString = AlleleManager.climateHelper.toDisplay(maxHumidity);
			return Translator.translateToLocal("for.mutation.condition.humidity.range").replace("%LOW", minHumidityString).replace("%HIGH", maxHumidityString);
		} else {
			return Translator.translateToLocalFormatted("for.mutation.condition.humidity.single", minHumidityString);
		}
	}
}
