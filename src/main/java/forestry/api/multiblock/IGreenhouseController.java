/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import java.util.List;
import java.util.Set;

import forestry.api.climate.IClimateControlProvider;
import forestry.api.climate.IClimateRegion;
import forestry.api.core.ICamouflageHandler;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IInternalBlock;

public interface IGreenhouseController extends IMultiblockController, IGreenhouseHousing, ICamouflageHandler, IClimateControlProvider {

	/**
	 * Handle change events.
	 */
	void onChange(EnumGreenhouseEventType type, Object event);

	/**
	 * @return The logics of the greenhouse.
	 */
	List<IGreenhouseLogic> getLogics();
	
	/**
	 * @return All internal blocks of the greenhouse.
	 */
	Set<IInternalBlock> getInternalBlocks();

	IClimateRegion getRegion();

}
