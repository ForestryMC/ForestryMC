/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.ILocatable;

public interface IClimateHousing extends IErrorLogicSource, ILocatable, IClimateProvider {

	/**
	 * @return the logic that handles the climate change of this housing.
	 */
	IClimateTransformer getTransformer();

	float getChangeForState(ClimateType type, IClimateManipulator manipulator);

	void markNetworkUpdate();
}
