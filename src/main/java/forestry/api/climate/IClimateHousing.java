/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.ILocatable;

public interface IClimateHousing extends IErrorLogicSource, ILocatable, IClimateProvider {

	/**
	 * @return The current temperature as an enum.
	 */
	EnumTemperature getTemperature();

	/**
	 * @return The current humidity as an enum.
	 */
	EnumHumidity getHumidity();

	/**
	 * @return The current temperature as an float. Range: 0.0F ~ 2.0F
	 */
	float getExactTemperature();

	/**
	 * @return The current humidity as an float. Range: 0.0F ~ 2.0F
	 */
	float getExactHumidity();

	/**
	 * @return the logic that handles the climate change of this housing.
	 */
	IClimateTransformer getTransformer();

	float getChangeForState(ClimateType type, IClimateManipulator manipulator);

	void markNetworkUpdate();
}
