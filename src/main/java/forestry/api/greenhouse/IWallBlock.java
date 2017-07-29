/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import net.minecraft.util.EnumFacing;

import forestry.api.climate.IClimateSource;

public interface IWallBlock extends IGreenhouseBlock {
	
	void setRoot(IBlankBlock parent);
	
	void setRootFace(EnumFacing facing);
	
	void onCreate();
	
	void onRemove();
	
	void add(IClimateSource source);
	
}
