/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import forestry.core.tiles.ILocatable;

public interface ICamouflagedBlock extends ILocatable {

	EnumCamouflageType getCamouflageType();
	
}
