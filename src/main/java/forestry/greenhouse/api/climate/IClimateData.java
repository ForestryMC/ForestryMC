/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import java.util.Map;

import forestry.api.climate.ClimateType;

public interface IClimateData {

	IClimateData addData(ClimateType type, String displayName, float value);

	/**
	 * @return A map with all data that this object contains.
	 */
	Map<String, Float> getData(ClimateType type);
}
