/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import java.util.List;

import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGreenhouseHelper {

	/**
	 * @return A {@link IGreenhouseController} of a greenhouse, when the pos is a greenhouse
	 */
	IGreenhouseController getGreenhouseController(World world, BlockPos pos);
	
	void addGreenhouseLogic(Class<? extends IGreenhouseLogic> logic);
	
	List<Class<? extends IGreenhouseLogic>> getGreenhouseLogics();
	
}
