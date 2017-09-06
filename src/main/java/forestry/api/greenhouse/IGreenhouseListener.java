/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import forestry.api.multiblock.IGreenhouseController;

public interface IGreenhouseListener {

	/**
	 * Called before the greenhouse updates the climate system.
	 *
	 * @param greenhouse The greenhouse of the listener.
	 * @param canWork    The actual {@link Boolean} value.
	 * @return True if the greenhouse can update the climate.
	 */
	boolean canWork(IGreenhouseController greenhouse, boolean canWork);

}
