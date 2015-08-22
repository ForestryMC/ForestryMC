package forestry.api.core;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashMap;
import java.util.Map;

public class BiomeHelper {

	private static final Map<BiomeGenBase, Boolean> isBiomeHellishCache = new HashMap<BiomeGenBase, Boolean>();

	/**
	 * Determines if it can rain or snow in the given biome.
	 */
	public static boolean canRainOrSnow(BiomeGenBase biomeGenBase) {
		return biomeGenBase.getEnableSnow() || biomeGenBase.canSpawnLightningBolt();
	}

	/**
	 * @deprecated since Forestry 3.2. Use canRainOrSnow(BiomeGenBase biomeGenBase)
	 */
	@Deprecated
	public static boolean canRainOrSnow(int biomeID) {
		return BiomeDictionary.isBiomeRegistered(biomeID) && canRainOrSnow(BiomeGenBase.getBiome(biomeID));
	}

	/**
	 * Determines if a given BiomeGenBase is of HELLISH temperature, since it is treated separately from actual temperature values.
	 * Uses the BiomeDictionary.
	 * @param biomeGen BiomeGenBase of the biome in question
	 * @return true, if the BiomeGenBase is a Nether-type biome; false otherwise.
	 */
	public static boolean isBiomeHellish(BiomeGenBase biomeGen) {
		if (isBiomeHellishCache.containsKey(biomeGen)) {
			return isBiomeHellishCache.get(biomeGen);
		}

		boolean isBiomeHellish = BiomeDictionary.isBiomeOfType(biomeGen, BiomeDictionary.Type.NETHER);
		isBiomeHellishCache.put(biomeGen, isBiomeHellish);
		return isBiomeHellish;
	}

	/**
	 * @deprecated since Forestry 3.2. Use isBiomeHellish(BiomeGenBase biomeGen)
	 */
	@Deprecated
	public static boolean isBiomeHellish(int biomeID) {
		return BiomeDictionary.isBiomeRegistered(biomeID) && isBiomeHellish(BiomeGenBase.getBiome(biomeID));
	}

}
