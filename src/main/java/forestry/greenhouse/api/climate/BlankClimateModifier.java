/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.IClimateState;
import forestry.api.climate.ImmutableClimateState;

public class BlankClimateModifier implements IClimateModifier {
	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, ImmutableClimateState oldState, NBTTagCompound data) {
		return newState;
	}

	@Override
	public void addData(IClimateContainer container, IClimateState climateState, NBTTagCompound nbtData, IClimateData data) {
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
