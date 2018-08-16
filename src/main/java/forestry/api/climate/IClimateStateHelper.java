/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Contains methods to create {@link IClimateState}s or to get constant {@link IClimateState}s like {@link #zero()} or
 * {@link #min()}.
 */
public interface IClimateStateHelper {

	IClimateState create(NBTTagCompound compound);

	IClimateState create(NBTTagCompound compound, boolean mutable);

	IClimateState create(float temperature, float humidity);

	IClimateState create(float temperature, float humidity, boolean mutable);

	IClimateState create(IClimateState climateState);

	IClimateState create(ClimateType type, float value);

	IClimateState create(IClimateState climateState, boolean mutable);

	NBTTagCompound writeToNBT(NBTTagCompound compound, IClimateState state);

	/**
	 * Checks if the given state is valid and returns the absent state if the given state is not valid.
	 */
	IClimateState checkState(IClimateState climateState);

	IClimateState clamp(IClimateState climateState);

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
