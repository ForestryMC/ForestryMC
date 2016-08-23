/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import javax.annotation.Nonnull;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import net.minecraft.util.math.BlockPos;

public interface IClimatePosition extends INbtReadable, INbtWritable {
	
	@Nonnull 
	IClimateRegion getClimateRegion();
	
	@Nonnull 
	BlockPos getPos();
	
	void setTemperature(float temperature);
	
	void addTemperature(float temperature);
	
	float getTemperature();

	void setHumidity(float humidity);
	
	void addHumidity(float humidity);
	
	float getHumidity();

}
