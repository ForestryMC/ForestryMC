/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.security.InvalidParameterException;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *  Many things Forestry use temperature and humidity of a biome to determine whether they can or how they can work or spawn at a given location.
 * 
 *  This enum concerns temperature.
 */
public enum EnumTemperature {
	NONE("None", "habitats/ocean"), ICY("Icy", "habitats/snow"), COLD("Cold", "habitats/taiga"),
	NORMAL("Normal", "habitats/plains"), WARM("Warm", "habitats/jungle"), HOT("Hot", "habitats/desert"), HELLISH("Hellish", "habitats/nether");

	public final String name;
	public final String iconIndex;

	private EnumTemperature(String name, String iconIndex) {
		this.name = name;
		this.iconIndex = iconIndex;
	}

	public String getName() {
		return this.name;
	}

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return ForestryAPI.textureManager.getDefault(iconIndex);
	}

	/**
	 * Determines if a given BiomeGenBase is of HELLISH temperature, since it is treated separately from actual temperature values.
	 * Uses the BiomeDictionary.
	 * @param biomeGen BiomeGenBase of the biome in question
	 * @return true, if the BiomeGenBase is a Nether-type biome; false otherwise.
	 * @deprecated since 3.2. Use BiomeHelper.isBiomeHellish(BiomeGenBase biomeGen)
	 */
	@Deprecated
	public static boolean isBiomeHellish(BiomeGenBase biomeGen) {
		return BiomeHelper.isBiomeHellish(biomeGen);
	}

	/**
	 * Determines if a given biomeID is of HELLISH temperature, since it is treated separately from actual temperature values.
	 * Uses the BiomeDictionary.
	 * @param biomeID ID of the BiomeGenBase in question
	 * @return true, if the biomeID is a Nether-type biome; false otherwise.
	 * @deprecated since 3.2. Use BiomeHelper.isBiomeHellish(BiomeGenBase biomeGen)
	 */
	@Deprecated
	@SuppressWarnings("deprecated")
	public static boolean isBiomeHellish(int biomeID) {
		return BiomeHelper.isBiomeHellish(biomeID);
	}

	/**
	 * Determines the EnumTemperature given a floating point representation of
	 * Minecraft temperature. Hellish biomes are handled based on their biome
	 * type - check BiomeHelper.isBiomeHellish.
	 * @param rawTemp raw temperature value
	 * @return EnumTemperature corresponding to value of rawTemp
	 */
	public static EnumTemperature getFromValue(float rawTemp) {
		if (rawTemp > 1.00f) {
			return HOT;
		}
		else if (rawTemp > 0.80f) {
			return WARM;
		}
		else if (rawTemp > 0.30f) {
			return NORMAL;
		}
		else if (rawTemp > 0.0f) {
			return COLD;
		}
		else {
			return ICY;
		}
	}

	public static EnumTemperature getFromBiome(BiomeGenBase biomeGenBase) {
		if (BiomeHelper.isBiomeHellish(biomeGenBase)) {
			return HELLISH;
		}
		return getFromValue(biomeGenBase.temperature);
	}

	/**
	 * @deprecated since Forestry 3.2. Use getFromBiome(BiomeGenBase biomeGenBase)
	 */
	@Deprecated
	public static EnumTemperature getFromBiome(int biomeID) {
		if (BiomeDictionary.isBiomeRegistered(biomeID))
			throw new InvalidParameterException("BiomeID is not registered: " + biomeID);
		return getFromBiome(BiomeGenBase.getBiome(biomeID));
	}

}
