/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import forestry.api.climate.IClimateState;
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
	NONE("None", "habitats/ocean"), ICY("Icy", "habitats/snow"), COLD("Cold", "habitats/taiga"),
	NORMAL("Normal", "habitats/plains"), WARM("Warm", "habitats/jungle"), HOT("Hot", "habitats/desert"), HELLISH("Hellish", "habitats/nether");

	public static EnumTemperature[] VALUES = values();
	
	public final String name;
	public final String iconIndex;

	EnumTemperature(String name, String iconIndex) {
		this.name = name;
		this.iconIndex = iconIndex;
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

	public static EnumTemperature getFromBiome(Biome biome, World world, BlockPos pos) {
		if (BiomeHelper.isBiomeHellish(biome)) {
			return HELLISH;
		}
		IClimateState state = ForestryAPI.climateManager.getClimateState(world, pos);
		float temperature = state.getTemperature();
		return getFromValue(temperature);
	}
}
