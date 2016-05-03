/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGreenhouseHelper {

	/**
	 * @return A {@link IGreenhouseState} of a greenhouse, when the pos is a greenhouse
	 */
	IGreenhouseState getGreenhouseState(World world, BlockPos pos);
	
}
