/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.world.biome.Biome;

import forestry.api.core.INbtWritable;

/**
 * A {@link IClimateState} is used to store and handle temperature and humidity.
 *
 * The values are oriented on the values of {@link Biome#getTemperature()} and {@link Biome#getRainfall()}.
 */
public interface IClimateState extends INbtWritable {

	float getTemperature();
	
	float getHumidity();
	
	IClimateState addTemperature(float temperature);

	IClimateState addHumidity(float humidity);
	
	IClimateState add(IClimateState state);

	IClimateState scale(double factor);

	IClimateState remove(IClimateState state);
	
	default float get(ClimateType type){
		return type == ClimateType.HUMIDITY ? getHumidity() : getTemperature();
	}

	IClimateState copy(ClimateStateType type);

	IClimateState copy();

	boolean isPresent();

	ClimateStateType getType();
	
}
