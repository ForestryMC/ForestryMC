/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import forestry.api.multiblock.IGreenhouseController;

public interface IGreenhouseLogicFactory {

	IGreenhouseLogic createLogic(IGreenhouseController controller);
	
}
