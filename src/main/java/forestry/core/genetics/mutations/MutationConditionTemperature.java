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

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

import forestry.api.climate.IClimateProvider;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IMutationCondition;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.core.utils.Translator;

public class MutationConditionTemperature implements IMutationCondition {

	private final EnumTemperature minTemperature;
	private final EnumTemperature maxTemperature;

	public MutationConditionTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature) {
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
	}

	@Override
	public float getChance(Level world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		EnumTemperature biomeTemperature = climate.getTemperature();

		if (biomeTemperature.ordinal() < minTemperature.ordinal() || biomeTemperature.ordinal() > maxTemperature.ordinal()) {
			return 0;
		}
		return 1;
	}

	@Override
	public Component getDescription() {
		//TODO textcomponent
		String minString = AlleleManager.climateHelper.toDisplay(minTemperature).getString();

		if (minTemperature != maxTemperature) {
			String maxString = AlleleManager.climateHelper.toDisplay(maxTemperature).getString();
			return Component.literal(Translator.translateToLocal("for.mutation.condition.temperature.range").replace("%LOW", minString).replace("%HIGH", maxString));
		} else {
			return Component.translatable("for.mutation.condition.temperature.single", minString);
		}
	}
}
