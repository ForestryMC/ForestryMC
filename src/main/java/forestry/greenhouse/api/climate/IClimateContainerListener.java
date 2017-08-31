/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

public interface IClimateContainerListener {

	/**
	 * Test if the container is closed.
	 */
	boolean isClosed(IClimateContainer container);
	
}
