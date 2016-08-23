/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateSourceProvider {
	
	IClimateSource getClimateSource();
	
	World getWorld();
	
	BlockPos getCoordinates();

}
