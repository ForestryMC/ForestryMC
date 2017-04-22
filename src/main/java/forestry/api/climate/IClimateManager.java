/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateManager {

	IClimateInfo createInfo(float temperature, float humidity);
	
	IClimateInfo getInfo(World world, BlockPos pos);

	IClimateProvider getDefaultClimate(World world, BlockPos pos);

	void addRegion(IClimateRegion region);

	void removeRegion(IClimateRegion region);

	void addSource(IClimateSourceProvider source);

	void removeSource(IClimateSourceProvider source);

	void onWorldUnload(World world);
	
	@Nullable
	IClimatePosition getPosition(World world, BlockPos pos);

	@Nullable
	IClimateRegion getRegionForPos(World world, BlockPos pos);

	Map<World, List<IClimateRegion>> getRegions();

}
