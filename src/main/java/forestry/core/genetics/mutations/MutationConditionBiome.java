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

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.StringUtil;

public class MutationConditionBiome implements IMutationCondition {

	private final List<BiomeDictionary.Type> validBiomeTypes;

	public MutationConditionBiome(BiomeDictionary.Type... types) {
		this.validBiomeTypes = Arrays.asList(types);
	}

	@Override
	public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		for (BiomeDictionary.Type type : validBiomeTypes) {
			if (BiomeDictionary.isBiomeOfType(biome, type)) {
				return 1;
			}
		}

		return 0;
	}

	@Override
	public String getDescription() {
		if (validBiomeTypes.size() > 1) {
			String biomeTypes = Arrays.toString(validBiomeTypes.toArray()).toLowerCase(Locale.ENGLISH);
			return StringUtil.localizeAndFormat("mutation.condition.biome.multiple", biomeTypes);
		} else {
			String biomeType = validBiomeTypes.get(0).toString().toLowerCase(Locale.ENGLISH);
			return StringUtil.localizeAndFormat("mutation.condition.biome.single", biomeType);
		}
	}
}
