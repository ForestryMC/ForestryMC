/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import forestry.api.multiblock.IGreenhouseController;

public interface IGreenhouseListener  {

	/**
	 * Called before the greenhouse work
	 * 
	 * @param greenhouse The greenhouse multiblock
	 * @return Can the greenhouse work
	 */
	<G extends IGreenhouseController & IGreenhouseHousing> boolean canWork(G greenhouse);
	
}
