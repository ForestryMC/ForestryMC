/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nullable;
import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGreenhouseHelper {

	/**
	 * @return A {@link IGreenhouseController} of a greenhouse, when the pos is a greenhouse
	 */
	@Nullable
	IGreenhouseController getGreenhouseController(World world, BlockPos pos);

}
