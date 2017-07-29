/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeHelper {

	private static final Map<Biome, Boolean> isBiomeHellishCache = new HashMap<>();

	/**
	 * Determines if a given Biome is of HELLISH temperature, since it is treated separately from actual temperature values.
	 * Uses the BiomeDictionary.
	 *
	 * @param biomeGen Biome of the biome in question
	 * @return true, if the Biome is a Nether-type biome; false otherwise.
	 */
	public static boolean isBiomeHellish(Biome biomeGen) {
		if (isBiomeHellishCache.containsKey(biomeGen)) {
			return isBiomeHellishCache.get(biomeGen);
		}

		boolean isBiomeHellish = BiomeDictionary.hasType(biomeGen, BiomeDictionary.Type.NETHER);
		isBiomeHellishCache.put(biomeGen, isBiomeHellish);
		return isBiomeHellish;
	}
}
