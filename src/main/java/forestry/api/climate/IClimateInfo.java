/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

/**
 * @deprecated TODO: Remove in 1.13,  Use IClimateState
 */
@Deprecated
public interface IClimateInfo {
	float getTemperature();

	float getHumidity();
}
