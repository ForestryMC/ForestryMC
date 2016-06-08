/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core.climate;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.climate.IClimateHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IClimatedPosition extends INbtReadable, INbtWritable {
	
	@Nonnull 
	IClimateWorld getClimateWorld();
	
	@Nonnull 
	BlockPos getPos();
	
	void setTemperature(float temperature);
	
	float getTemperature();

	void setHumidity(float humidity);
	
	float getHumidity();
	
	List<IClimateHandler> getHandlers();
	
	void updateClimate();

}
