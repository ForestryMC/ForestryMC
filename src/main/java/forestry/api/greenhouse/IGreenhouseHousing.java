/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import forestry.api.core.IErrorLogicSource;
import forestry.core.tiles.IClimatised;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IGreenhouseHousing extends IErrorLogicSource, IClimatised {

	World getWorld();
	
	boolean isInGreenhouse(BlockPos pos);
	
}
