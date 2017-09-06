/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

public interface IGreenhouseLogic extends INbtReadable, INbtWritable{

	void work(int ticks);
	
	String getUID();
	
}
