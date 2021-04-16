/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

/**
 * Provides exact climate information about an object (tile entity or multiblock)
 */
public interface IClimatised extends IRoughClimateProvider {

	/**
	 * The current temperature of this object represented by an enum.
	 * <p>
	 * {@link EnumTemperature#HELLISH} if the biome of the object is based in the nether.
	 *
	 * @return An enum value based on the temperature of this object.
	 */
	default EnumTemperature getTemperature() {
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	/**
	 * The current humidity of this object represented by an enum.
	 *
	 * @return An enum value based on the humidity of this object.
	 */
	default EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	/**
	 * The current temperature value of this object.
	 * The range is based on the vanilla values of the biomes.
	 *
	 * @return A value between 0.0f and 2.0f.
	 */
	float getExactTemperature();

	/**
	 * The current humidity value of this object.
	 * The range is based on the vanilla values of the biomes.
	 *
	 * @return A value between 0.0f and 2.0f.
	 */
	float getExactHumidity();
}
