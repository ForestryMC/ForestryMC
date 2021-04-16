/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;


/**
 * Provides rough climate information, for more exact data use {@link IClimatised} or {@link IClimateProvider}
 */
public interface IRoughClimateProvider {
	/**
	 * The current temperature of this object represented by an enum.
	 * <p>
	 * {@link EnumTemperature#HELLISH} if the biome of the object is based in the nether.
	 *
	 * @return An enum value based on the temperature of this object.
	 */
	EnumTemperature getTemperature();

	/**
	 * The current humidity of this object represented by an enum.
	 *
	 * @return An enum value based on the humidity of this object.
	 */
	EnumHumidity getHumidity();
}
