/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.function.Function;

import net.minecraft.world.biome.Biome;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

/**
 * A {@link IClimateState} is used to store and handle temperature and humidity.
 * <p>
 * The values are oriented on the values of {@link Biome#getDefaultTemperature()} and {@link Biome#getRainfall()}.
 * <p>
 * If any of the two values would be Float.NAN after an operation {@link IClimateStateHelper#absent()} will be returned
 * instead of the state.
 * <p>
 * {@link IClimateStateHelper} at {@link ClimateManager#stateHelper} provides some helper methods to create and handle
 * climate states.
 */
public interface IClimateState {

	/**
	 * @return Returns the exact temperature value of this climate state.
	 */
	float getTemperature();

	/**
	 * @return Returns the enum temperature value of this climate state.
	 */
	default EnumTemperature getTemperatureEnum() {
		return EnumTemperature.getFromValue(getTemperature());
	}

	/**
	 * @return Returns the exact humidity value of this climate state.
	 */
	float getHumidity();

	/**
	 * @return Returns the enum humidity value of this climate state.
	 */
	default EnumHumidity getHumidityEnum() {
		return EnumHumidity.getFromValue(getHumidity());
	}

	/**
	 * @return Returns the climate value of the given type.
	 */
	default float getClimate(ClimateType type) {
		return type == ClimateType.HUMIDITY ? getHumidity() : getTemperature();
	}

	/**
	 * Adds the given value to the temperature value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState addTemperature(float temperature);

	/**
	 * Adds the given value to the humidity value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState addHumidity(float humidity);

	/**
	 * Adds the given state to this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState add(IClimateState state);

	/**
	 * Adds the given value to the value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	default IClimateState add(ClimateType type, float value) {
		return type == ClimateType.HUMIDITY ? addHumidity(value) : addTemperature(value);
	}

	/**
	 * Sets the temperature value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState setTemperature(float temperature);

	/**
	 * Sets the humidity value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState setHumidity(float humidity);

	/**
	 * Sets the humidity and temperature value to the given values.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState setClimate(float temperature, float humidity);

	/**
	 * Adds climate value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	default IClimateState setClimate(ClimateType type, float value) {
		return type == ClimateType.HUMIDITY ? setHumidity(value) : setTemperature(value);
	}

	/**
	 * Multiplies the values of this state with the given factor.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState multiply(double factor);

	/**
	 * Subtracts the given value from the temperature value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	default IClimateState subtractTemperature(float value) {
		return addTemperature(-value);
	}

	/**
	 * Subtracts the given value from the humidity value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	default IClimateState subtractHumidity(float value) {
		return addHumidity(-value);
	}

	/**
	 * Subtracts the given state from this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState subtract(IClimateState state);

	/**
	 * Subtracts the given value from the value of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	default IClimateState subtract(ClimateType type, float value) {
		return type == ClimateType.HUMIDITY ? subtractHumidity(value) : subtractTemperature(value);
	}

	/**
	 * Applies the mapper {@link Function#apply(Object)} on the two climate values of this state.
	 *
	 * @return The absent state if any of the two climate values would be Float.NAN otherwise if this state is
	 * {@link #isMutable()} this state or if it isn't a immutable copy of the state with the new values.
	 */
	IClimateState map(Function<Float, Float> mapper);

	/**
	 * @param mutable If the copy of this state should be mutable or immutable.
	 * @return The copy of this state.
	 */
	IClimateState copy(boolean mutable);

	/**
	 * @return The copy of this state if {@link #isMutable()} of this state is true or returns the immutable state.
	 */
	IClimateState copy();

	/**
	 * @return A mutable copy of this state if the state is immutable and the state itself if the state is mutable.
	 */
	IClimateState toMutable();

	/**
	 * @return A immutable copy of this state of the state is mutable and the state itself if the state is immutable.
	 */
	IClimateState toImmutable();

	/**
	 * @return If any of the two climate values of this state is Float.NAN.
	 */
	boolean isPresent();

	/**
	 * @return If the state is mutable.
	 */
	boolean isMutable();

	/**
	 * @return If the two values of this state are between 0 and 2.0.
	 * @see IClimateStateHelper#clamp(IClimateState)
	 */
	boolean isClamped();
}
