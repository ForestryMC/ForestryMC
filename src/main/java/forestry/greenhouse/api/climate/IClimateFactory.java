/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import forestry.api.greenhouse.IClimateHousing;

public interface IClimateFactory {

	//Add this to the api
	/**
	 * A factory to create climate related things.
	 * @since 5.3.4
	 */
	//public static IClimateFactory climateFactory;

	/**
	 * @param climatedRegion
	 * @return Creates a climate container.
	 */
	IClimateContainer createContainer(IClimateHousing climatedRegion);

}
