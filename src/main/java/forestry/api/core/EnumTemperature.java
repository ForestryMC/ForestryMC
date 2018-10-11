/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Many things Forestry use temperature and humidity of a biome to determine whether they can or how they can work or spawn at a given location.
 * <p>
 * This enum concerns temperature.
 */
public enum EnumTemperature {
	NONE("None", "habitats/ocean", 0x808080), ICY("Icy", "habitats/snow", 0xaafff0), COLD("Cold", "habitats/taiga", 0x72ddf7),
	NORMAL("Normal", "habitats/plains", 0xffd013), WARM("Warm", "habitats/jungle", 0xfb8a24), HOT("Hot", "habitats/desert", 0xd61439), HELLISH("Hellish", "habitats/nether", 0x81032d);

	public static EnumTemperature[] VALUES = values();
	
	public final String name;
	public final String iconIndex;
	public final int color;

	EnumTemperature(String name, String iconIndex, int color) {
		this.name = name;
		this.iconIndex = iconIndex;
		this.color = color;
	}

	public String getName() {
		return this.name;
	}

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getSprite() {
		return ForestryAPI.textureManager.getDefault(iconIndex);
	}

	/**
	 * Determines the EnumTemperature given a floating point representation of
	 * Minecraft temperature. Hellish biomes are handled based on their biome
	 * type - check BiomeHelper.isBiomeHellish.
	 *
	 * @param rawTemp raw temperature value
	 * @return EnumTemperature corresponding to value of rawTemp
	 */
	public static EnumTemperature getFromValue(float rawTemp) {
		if (rawTemp > 1.00f) {
			return HOT;
		} else if (rawTemp > 0.85f) {
			return WARM;
		} else if (rawTemp > 0.35f) {
			return NORMAL;
		} else if (rawTemp > 0.0f) {
			return COLD;
		} else {
			return ICY;
		}
	}

	public static EnumTemperature getFromBiome(Biome biome) {
		if (BiomeHelper.isBiomeHellish(biome)) {
			return HELLISH;
		}
		return getFromValue(biome.getDefaultTemperature());
	}

	/**
	 * @deprecated Use the version below.
	 */
	@Deprecated
	public static EnumTemperature getFromBiome(Biome biome, World world, BlockPos pos) {
		return getFromBiome(biome, pos);
	}

	public static EnumTemperature getFromBiome(Biome biome, BlockPos pos) {
		if (BiomeHelper.isBiomeHellish(biome)) {
			return HELLISH;
		}
		float temperature = biome.getTemperature(pos);
		return getFromValue(temperature);
	}
}
