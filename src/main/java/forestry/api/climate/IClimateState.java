/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.function.Function;

import net.minecraft.world.biome.Biome;

/**
 * A {@link IClimateState} is used to store and handle temperature and humidity.
 * <p>
 * The values are oriented on the values of {@link Biome#getDefaultTemperature()} and {@link Biome#getRainfall()}.
 */
public interface IClimateState {

	float getTemperature();

	float getHumidity();

	IClimateState addTemperature(float temperature);

	IClimateState addHumidity(float humidity);

	IClimateState add(IClimateState state);

	default IClimateState add(ClimateType type, float value) {
		return type == ClimateType.HUMIDITY ? addHumidity(value) : addTemperature(value);
	}

	IClimateState setTemperature(float temperature);

	IClimateState setHumidity(float humidity);

	IClimateState setClimate(float temperature, float humidity);

	default IClimateState setClimate(ClimateType type, float value) {
		return type == ClimateType.HUMIDITY ? setHumidity(value) : setTemperature(value);
	}

	IClimateState multiply(double factor);

	IClimateState subtract(IClimateState state);

	default IClimateState subtract(ClimateType type, float value) {
		return type == ClimateType.HUMIDITY ? subtractHumidity(value) : subtractTemperature(value);
	}

	IClimateState map(Function<Float, Float> mapper);

	default IClimateState subtractTemperature(float value) {
		return addTemperature(-value);
	}

	default IClimateState subtractHumidity(float value) {
		return addHumidity(-value);
	}

	default float getClimate(ClimateType type) {
		return type == ClimateType.HUMIDITY ? getHumidity() : getTemperature();
	}

	IClimateState copy(boolean mutable);

	IClimateState copy();

	IClimateState toMutable();

	IClimateState toImmutable();

	boolean isPresent();

	boolean isMutable();

	boolean isClamped();
}
