/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.Collection;
import javax.annotation.Nullable;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateRegion extends INbtReadable, INbtWritable {

	World getWorld();

	int getTicksPerUpdate();

	void setPosition(BlockPos pos, float temperature, float humidity);
	
	@Nullable
	IClimatePosition getPosition(BlockPos pos);
	
	Collection<IClimatePosition> getPositions();

	void addSource(IClimateSource source);

	void removeSource(IClimateSource source);
	
	Collection<IClimateSource> getSources();

	void calculateAverageClimate();
	
	float getAverageTemperature();

	float getAverageHumidity();
	
	/**
	 * Update the climate in a region.
	 */
	void updateClimate(int ticks);

}
