/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateManager {

	float getTemperature(World world, BlockPos pos);
	
	float getHumidity(World world, BlockPos pos);

	IClimateProvider getDefaultClimate(World world, BlockPos pos);

}
