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

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IMutationCondition;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

public class MutationConditionBiome implements IMutationCondition {

	private final Set<Biome.BiomeCategory> validBiomeTypes;

	public MutationConditionBiome(Biome.BiomeCategory... types) {
		this.validBiomeTypes = Set.of(types);
	}

	@Override
	public float getChance(Level world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		Biome biome = climate.getBiome();
		Biome.BiomeCategory biomeCategory = biome.getBiomeCategory();
		if (validBiomeTypes.contains(biomeCategory)) {
			return 1;
		}

		return 0;
	}

	@Override
	public Component getDescription() {
		if (validBiomeTypes.size() > 1) {
			String biomeTypes = Arrays.toString(validBiomeTypes.toArray()).toLowerCase(Locale.ENGLISH);
			return Component.translatable("for.mutation.condition.biome.multiple", biomeTypes);
		} else {
			Biome.BiomeCategory firstCategory = validBiomeTypes.iterator().next();
			String biomeType = firstCategory.toString().toLowerCase(Locale.ENGLISH);
			return Component.translatable("for.mutation.condition.biome.single", biomeType);
		}
	}
}
