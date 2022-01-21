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
import java.util.List;
import java.util.Locale;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IMutationCondition;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;

public class MutationConditionBiome implements IMutationCondition {

	private final List<Biome.BiomeCategory> validBiomeTypes;

	public MutationConditionBiome(Biome.BiomeCategory... types) {
		this.validBiomeTypes = Arrays.asList(types);
	}

	@Override
	public float getChance(Level world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		Biome biome = climate.getBiome();
		for (Biome.BiomeCategory category : validBiomeTypes) {
			if (biome.getBiomeCategory() == category) {
				return 1;
			}
		}

		return 0;
	}

	@Override
	public Component getDescription() {
		if (validBiomeTypes.size() > 1) {
			String biomeTypes = Arrays.toString(validBiomeTypes.toArray()).toLowerCase(Locale.ENGLISH);
			return new TranslatableComponent("for.mutation.condition.biome.multiple", biomeTypes);
		} else {
			String biomeType = validBiomeTypes.get(0).toString().toLowerCase(Locale.ENGLISH);
			return new TranslatableComponent("for.mutation.condition.biome.single", biomeType);
		}
	}
}
