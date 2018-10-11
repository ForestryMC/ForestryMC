/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Contains methods to create {@link IClimateState}s or to get constant and immutable {@link IClimateState}s like
 * {@link #zero()}, {@link #min()},  {@link #max()} or {@link #absent()}.
 */
public interface IClimateStateHelper {

	/**
	 * Creates a climate state of the given two climate values.
	 *
	 * @param temperature The temperature value of the new state.
	 * @param humidity    The humidity value of the new state.
	 * @return A newly created state that contains the given values.
	 */
	IClimateState create(float temperature, float humidity);

	/**
	 * Creates a climate state of the given two climate values.
	 *
	 * @param temperature The temperature value of the new state.
	 * @param humidity    The humidity value of the new state.
	 * @param mutable     If the new state should be mutable.
	 * @return A newly created state that contains the given values.
	 */
	IClimateState create(float temperature, float humidity, boolean mutable);

	/**
	 * Creates a copy of the given state.
	 *
	 * @param climateState The state that should be copied.
	 * @return A copy of the given state.
	 */
	IClimateState create(IClimateState climateState);

	/**
	 * Creates a copy of the given state.
	 *
	 * @param climateState The state that should be copied.
	 * @param mutable      If the copy should be mutable.
	 * @return A copy of the given state.
	 */
	IClimateState create(IClimateState climateState, boolean mutable);

	/**
	 * Creates a state that contains the given value of the given type and 0.0F as the value of the other type.
	 *
	 * @param type  The type of the given value.
	 * @param value The value of the given type.
	 * @return A state that contains the given value of the given type and 0.0F as the value of the other type.
	 */
	IClimateState create(ClimateType type, float value);

	/**
	 * Creates a state based on the data that the given compound contains.
	 *
	 * @param compound The compound that contains the data.
	 * @return A state that contains the data that the compound contains.
	 */
	IClimateState create(NBTTagCompound compound);

	/**
	 * Creates a state based on the data that the given compound contains.
	 *
	 * @param compound The compound that contains the data.
	 * @param mutable  If the copy should be mutable.
	 * @return A state that contains the data that the compound contains.
	 */
	IClimateState create(NBTTagCompound compound, boolean mutable);

	/**
	 * Writes the data of the given state to the given compound.
	 *
	 * @param compound The compound that the data will be writen to.
	 * @param state    The state that contains the data.
	 * @return The given compound.
	 */
	NBTTagCompound writeToNBT(NBTTagCompound compound, IClimateState state);

	/**
	 * Checks if the given state is valid and returns the absent state if the given state is not valid.
	 */
	IClimateState checkState(IClimateState climateState);

	/**
	 * @return Clamps the values of the given state between 0.0F and 2.0F.
	 */
	IClimateState clamp(IClimateState climateState);

	/**
	 * @return The absent climate state. Both values of the absent state are Float.NAN.
	 */
	IClimateState absent();

	/**
	 * @return the minimal values of an immutable {@link IClimateState}.
	 */
	IClimateState min();

	/**
	 * @return the maximal values of an immutable {@link IClimateState}.
	 */
	IClimateState max();

	/**
	 * @return a immutable climate state of which both {@link IClimateState#getHumidity()} and
	 * {@link IClimateState#getTemperature()} are zero.
	 */
	IClimateState zero();

	/**
	 * @return a mutable climate state of which both {@link IClimateState#getHumidity()} and
	 * {@link IClimateState#getTemperature()} are zero.
	 */
	IClimateState mutableZero();
}
