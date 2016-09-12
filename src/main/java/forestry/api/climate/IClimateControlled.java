/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

public interface IClimateControlled {
	/**
	 * Change the temperature, but not lower than boundaryDown or higher than boundaryUp.
	 */
	void addTemperatureChange(float change, float boundaryDown, float boundaryUp);

	/**
	 * Change the humidity, but not lower than boundaryDown or higher than boundaryUp.
	 */
	void addHumidityChange(float change, float boundaryDown, float boundaryUp);
}
