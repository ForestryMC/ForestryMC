/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.world.biome.Biome;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

public interface IClimateProvider {
	Biome getBiome();

	EnumTemperature getTemperature();

	EnumHumidity getHumidity();
}
