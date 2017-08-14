/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

public interface IClimateFactory {

	/**
	 * @param climatedRegion
	 * 
	 * @return Creates a climate container.
	 */
	IClimateContainer createContainer(IClimateHousing climatedRegion);
	
}
