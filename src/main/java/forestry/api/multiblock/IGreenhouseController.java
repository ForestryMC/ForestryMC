/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import java.util.List;

import javax.annotation.Nullable;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.greenhouse.IGreenhouseLogic;

public interface IGreenhouseController extends IMultiblockController, IGreenhouseHousing, ICamouflageHandler {
	
	/**
	 * Handle change events.
	 */
	void onChange(EnumGreenhouseEventType type, Object event);
	
	/**
	 * @return The logics of the greenhouse.
	 */
	List<IGreenhouseLogic> getLogics();
	
	@Nullable
	IClimateRegion getRegion();

}
