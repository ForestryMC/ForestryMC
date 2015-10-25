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
package forestry.core.genetics;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;

public class MutationConditionTemperature implements IMutationCondition {

	private final EnumTemperature minTemperature;
	private final EnumTemperature maxTemperature;

	public MutationConditionTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature) {
		this.minTemperature = minTemperature;
		this.maxTemperature = maxTemperature;
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0,
			IGenome genome1) {
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenerator(pos);
		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, pos);

		if (biomeTemperature.ordinal() < minTemperature.ordinal()
				|| biomeTemperature.ordinal() > maxTemperature.ordinal()) {
			return 0;
		}
		return 1;
	}

	@Override
	public String getDescription() {
		if (minTemperature != maxTemperature) {
			return String.format("Temperature between %s and %s.", minTemperature, maxTemperature);
		} else {
			return String.format("Temperature %s required.", minTemperature);
		}
	}
}
