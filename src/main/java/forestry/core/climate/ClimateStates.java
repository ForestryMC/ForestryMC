/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.climate;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateStates;

public final class ClimateStates implements IClimateStates {

	public static final ClimateStates INSTANCE = new ClimateStates();

	private ClimateStates() {
	}

	public static IClimateState of(float temperature, float humidity, ClimateStateType type) {
		IClimateState state = INSTANCE.create(temperature, humidity, type);
		if (!state.isPresent()) {
			return INSTANCE.absent();
		}
		return state;
	}

	public static IClimateState of(float temperature, float humidity) {
		IClimateState state = INSTANCE.create(temperature, humidity, ClimateStateType.MUTABLE);
		if (!state.isPresent()) {
			return INSTANCE.absent();
		}
		return state;
	}

	public static IClimateState immutableOf(float temperature, float humidity) {
		IClimateState state = INSTANCE.create(temperature, humidity, ClimateStateType.IMMUTABLE);
		if (!state.isPresent()) {
			return INSTANCE.absent();
		}
		return state;
	}

	public static IClimateState changeOf(float temperature, float humidity) {
		IClimateState state = INSTANCE.create(temperature, humidity, ClimateStateType.CHANGE);
		if (!state.isPresent()) {
			return INSTANCE.absent();
		}
		return state;
	}

	@Override
	public IClimateState create(IClimateState climateState, ClimateStateType type) {
		return new ClimateState(climateState, type);
	}

	@Override
	public IClimateState create(float temperature, float humidity, ClimateStateType type) {
		return new ClimateState(temperature, humidity, type);
	}

	@Override
	public IClimateState create(NBTTagCompound compound, ClimateStateType type) {
		if(compound.getBoolean(ClimateState.ABSENT_NBT_KEY)){
			return AbsentClimateState.INSTANCE;
		}
		return new ClimateState(compound, type);
	}

	@Override
	public IClimateState create(NBTTagCompound compound) {
		if(compound.getBoolean(ClimateState.ABSENT_NBT_KEY)){
			return AbsentClimateState.INSTANCE;
		}
		return new ClimateState(compound);
	}

	@Override
	public IClimateState absent() {
		return AbsentClimateState.INSTANCE;
	}

	@Override
	public IClimateState min() {
		return ClimateState.MIN;
	}

	@Override
	public IClimateState max() {
		return ClimateState.MAX;
	}
}
