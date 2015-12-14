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

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.EnumHumidity;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.StringUtil;

public class MutationConditionHumidity implements IMutationCondition {
	private final EnumHumidity minHumidity;
	private final EnumHumidity maxHumidity;

	public MutationConditionHumidity(EnumHumidity minHumidity, EnumHumidity maxHumidity) {
		this.minHumidity = minHumidity;
		this.maxHumidity = maxHumidity;
	}

	@Override
	public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.rainfall);

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
			return StringUtil.localize("mutation.condition.humidity.range").replace("%LOW", minHumidityString).replace("%HIGH", maxHumidityString);
		} else {
			return StringUtil.localizeAndFormat("mutation.condition.humidity.single", minHumidityString);
		}
	}
}
