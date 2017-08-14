/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

public interface IClimateContainerListener {

	/**
	 * Test if the container is closed.
	 *
	 * @return true to let the container slowly set his climate state back to the default climate state.
	 */
	boolean isClosed(IClimateContainer container);
	
}
