/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.nbt.NBTTagCompound;

public interface IClimateStates {

	IClimateState create(NBTTagCompound compound);

	IClimateState create(NBTTagCompound compound, ClimateStateType type);

	IClimateState create(float temperature, float humidity, ClimateStateType type);

	IClimateState create(IClimateState climateState, ClimateStateType type);

	IClimateState absent();

	IClimateState min();

	IClimateState max();
}
