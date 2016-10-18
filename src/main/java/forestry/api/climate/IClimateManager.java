/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateManager {

	float getTemperature(World world, BlockPos pos);
	
	float getHumidity(World world, BlockPos pos);
	
	@Nonnull
	IClimateProvider getDefaultClimate(World world, BlockPos pos);
	
	void addRegion(IClimateRegion region);
	
	void removeRegion(IClimateRegion region);
	
	void addSource(IClimateSourceProvider source);
	
	void removeSource(IClimateSourceProvider source);
	
	@Nullable
	IClimatePosition getPosition(World world, BlockPos pos);
	
	@Nullable
	IClimateRegion getRegionForPos(World world, BlockPos pos);
	
	@Nonnull
	Map<Integer, List<IClimateRegion>> getRegions();
	
}
