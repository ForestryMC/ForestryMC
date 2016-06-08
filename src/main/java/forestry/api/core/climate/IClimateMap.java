/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core.climate;

import java.util.List;
import java.util.Stack;

import forestry.core.multiblock.RectangularMultiblockControllerBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateMap {

	List<IClimatedPosition> getPositions();
	
	World getWorld();
	
	BlockPos getMinimumPos();
	
	BlockPos getMaximumPos();
	
	void updateClimate();
	
}
