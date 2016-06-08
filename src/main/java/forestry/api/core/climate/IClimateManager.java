/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core.climate;

import java.util.Map;

import forestry.api.core.climate.IClimateWorld.ClimateChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateManager {

	float getTemperature(World world, BlockPos pos);
	
	float getHumidity(World world, BlockPos pos);
	
	void registerWorld(IClimateWorld climateWorld);
	
	Map<Integer, IClimateWorld> getClimateWorlds();
	
}
