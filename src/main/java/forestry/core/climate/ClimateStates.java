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
import forestry.greenhouse.climate.modifiers.ClimateSourceModifier;

public final class ClimateStates implements IClimateStates {

	public static final ClimateStates INSTANCE = new ClimateStates();
	public static final IClimateState ZERO = of(0.0F, 0.0F);

	private ClimateStates() {
	}

	public static IClimateState of(float temperature, float humidity, ClimateStateType type) {
		return INSTANCE.create(temperature, humidity, type);
	}

	public static IClimateState of(float temperature, float humidity) {
		return INSTANCE.create(temperature, humidity, ClimateStateType.DEFAULT);
	}

	public static IClimateState extendedOf(float temperature, float humidity) {
		return INSTANCE.create(temperature, humidity, ClimateStateType.EXTENDED);
	}

	public static IClimateState extendedZero() {
		return INSTANCE.create(ZERO, ClimateStateType.EXTENDED);
	}

	public static boolean isNearTarget(IClimateState state, IClimateState target) {
		return target.getHumidity() - ClimateSourceModifier.CLIMATE_CHANGE < state.getHumidity()
				&& target.getHumidity() + ClimateSourceModifier.CLIMATE_CHANGE > state.getHumidity()
				&& target.getTemperature() - ClimateSourceModifier.CLIMATE_CHANGE < state.getTemperature()
				&& target.getTemperature() + ClimateSourceModifier.CLIMATE_CHANGE > state.getTemperature();
	}

	public static boolean isZero(IClimateState state) {
		return state.getHumidity() == ZERO.getHumidity() && state.getTemperature() == ZERO.getTemperature();
	}

	public static boolean isNearZero(IClimateState state) {
		return isNearTarget(state, ZERO);
	}

	@Override
	public IClimateState create(IClimateState climateState, ClimateStateType type) {
		IClimateState state = new ClimateState(climateState, type);
		if (!state.isPresent()) {
			return absent();
		}
		return state;
	}

	@Override
	public IClimateState create(float temperature, float humidity, ClimateStateType type) {
		IClimateState state = new ClimateState(temperature, humidity, type);
		if (!state.isPresent()) {
			return absent();
		}
		return state;
	}

	@Override
	public IClimateState create(NBTTagCompound compound, ClimateStateType type) {
		if (compound.getBoolean(ClimateState.ABSENT_NBT_KEY)) {
			return AbsentClimateState.INSTANCE;
		}
		return new ClimateState(compound, type);
	}

	@Override
	public IClimateState create(NBTTagCompound compound) {
		if (compound.getBoolean(ClimateState.ABSENT_NBT_KEY)) {
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
