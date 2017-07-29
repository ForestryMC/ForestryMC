/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nullable;

import forestry.api.climate.IClimateHousing;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogicSource;

public interface IGreenhouseHousing extends IErrorLogicSource, IClimateHousing{
	
	/**
	 * @return The current tempreture as an enum.
	 */
	EnumTemperature getTemperature();
	
	/**
	 * @return The current humidity as an enum.
	 */
	EnumHumidity getHumidity();
	
	/**
	 * @return The current tempreture as an float. Range: 0.0F ~ 2.0F
	 */
	float getExactTemperature();
	
	/**
	 * @return The current humidity as an float. Range: 0.0F ~ 2.0F
	 */
	float getExactHumidity();
	
	/**
	 * @return the manager of this housing.
	 */
	IGreenhouseProvider getProvider();
	
	/**
	 * @return the maximal and minimal settings of this housing.
	 */
	@Nullable
	IGreenhouseLimits getLimits();
}
