/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import forestry.api.multiblock.IGreenhouseController;

public class DefaultGreenhouseListener implements IGreenhouseListener {

	@Override
	public <G extends IGreenhouseController & IGreenhouseHousing> boolean canWork(G greenhouse) {
		return true;
	}

}
