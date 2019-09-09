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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateStateHelper;

public final class ClimateStateHelper implements IClimateStateHelper {

	private static final String TEMPERATURE_NBT_KEY = "TEMP";
	private static final String HUMIDITY_NBT_KEY = "HUMID";
	private static final String ABSENT_NBT_KEY = "ABSENT";
	private static final String MUTABLE_NBT_KEY = "MUTABLE";

	public static final ClimateStateHelper INSTANCE = new ClimateStateHelper();
	public static final IClimateState ZERO_STATE = ImmutableClimateState.MIN;
	public static final float CLIMATE_CHANGE = 0.01F;

	private ClimateStateHelper() {
	}

	public static IClimateState of(float temperature, float humidity, boolean mutable) {
		return INSTANCE.create(temperature, humidity, mutable);
	}

	public static IClimateState of(float temperature, float humidity) {
		return INSTANCE.create(temperature, humidity, false);
	}

	public static IClimateState mutableOf(float temperature, float humidity) {
		return INSTANCE.create(temperature, humidity, true);
	}

	public static boolean isNearTarget(IClimateState state, IClimateState target) {
		return isNearTarget(ClimateType.HUMIDITY, state, target) && isNearTarget(ClimateType.TEMPERATURE, state, target);
	}

	public static boolean isNearTarget(ClimateType type, IClimateState state, IClimateState target) {
		float targetedValue = target.getClimate(type);
		float value = state.getClimate(type);
		return targetedValue - CLIMATE_CHANGE < value && targetedValue + CLIMATE_CHANGE > value;
	}

	public static boolean isZero(IClimateState state) {
		return state.getHumidity() == ZERO_STATE.getHumidity() && state.getTemperature() == ZERO_STATE.getTemperature();
	}

	public static boolean isNearZero(ClimateType type, IClimateState state) {
		return isNearTarget(type, state, ZERO_STATE);
	}

	public static boolean isZero(ClimateType type, IClimateState state) {
		return state.getClimate(type) == ZERO_STATE.getClimate(type);
	}

	public static boolean isNearZero(IClimateState state) {
		return isNearTarget(state, ZERO_STATE);
	}

	@Override
	public IClimateState create(float temperature, float humidity) {
		return create(temperature, humidity, false);
	}

	@Override
	public IClimateState create(ClimateType type, float value) {
		return create(type == ClimateType.TEMPERATURE ? value : 0.0F, type == ClimateType.HUMIDITY ? value : 0.0F);
	}

	@Override
	public IClimateState create(IClimateState climateState) {
		return create(climateState, false);
	}

	@Override
	public IClimateState create(IClimateState climateState, boolean mutable) {
		return create(climateState.getTemperature(), climateState.getHumidity(), mutable);
	}

	@Override
	public IClimateState create(float temperature, float humidity, boolean mutable) {
		IClimateState state;
		if (mutable) {
			state = new MutableClimateState(temperature, humidity);
		} else {
			state = new ImmutableClimateState(temperature, humidity);
		}
		return checkState(state);
	}

	@Override
	public IClimateState create(CompoundNBT compound, boolean mutable) {
		if (compound.getBoolean(ABSENT_NBT_KEY)) {
			return AbsentClimateState.INSTANCE;
		}
		return checkState(create(compound.getFloat(TEMPERATURE_NBT_KEY), compound.getFloat(HUMIDITY_NBT_KEY), mutable));
	}

	@Override
	public IClimateState create(CompoundNBT compound) {
		return create(compound, compound.getBoolean(MUTABLE_NBT_KEY));
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT compound, IClimateState state) {
		if (state.isPresent()) {
			compound.putBoolean(ABSENT_NBT_KEY, false);
			compound.putFloat(TEMPERATURE_NBT_KEY, state.getTemperature());
			compound.putFloat(HUMIDITY_NBT_KEY, state.getHumidity());
			compound.putBoolean(MUTABLE_NBT_KEY, state instanceof MutableClimateState);
		} else {
			compound.putBoolean(ABSENT_NBT_KEY, true);
		}
		return compound;
	}

	@Override
	public IClimateState checkState(IClimateState climateState) {
		return !climateState.isPresent() ? absent() : climateState;
	}

	@Override
	public IClimateState clamp(IClimateState climateState) {
		if (!climateState.isPresent()) {
			return absent();
		}
		float temp = climateState.getTemperature();
		float humid = climateState.getHumidity();
		if (temp > 2.0F || temp < 0.0F || humid > 2.0F || humid < 0.0F) {
			return climateState.setClimate(MathHelper.clamp(temp, 0.0F, 2.0F), MathHelper.clamp(humid, 0.0F, 2.0F));
		}
		return climateState;
	}

	@Override
	public IClimateState absent() {
		return AbsentClimateState.INSTANCE;
	}

	@Override
	public IClimateState min() {
		return ImmutableClimateState.MIN;
	}

	@Override
	public IClimateState max() {
		return ImmutableClimateState.MAX;
	}

	@Override
	public IClimateState zero() {
		return ZERO_STATE;
	}

	@Override
	public IClimateState mutableZero() {
		return create(0.0F, 0.0F, true);
	}
}
