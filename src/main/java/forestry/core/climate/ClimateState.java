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

import com.google.common.base.MoreObjects;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimateState;

class ClimateState implements IClimateState, IClimateInfo {

	// The minimum climate state.
	public static final ClimateState MIN = new ClimateState(0.0F, 0.0F, ClimateStateType.DEFAULT);
	// The maximum climate state.
	public static final ClimateState MAX = new ClimateState(2.0F, 2.0F, ClimateStateType.DEFAULT);

	public static final String TEMPERATURE_NBT_KEY = "TEMP";
	public static final String HUMIDITY_NBT_KEY = "HUMID";
	public static final String TYPE_NBT_KEY = "TYPE";
	public static final String ABSENT_NBT_KEY = "ABSENT";

	protected final ClimateStateType type;
	protected float temperature;
	protected float humidity;

	public ClimateState(IClimateState climateState, ClimateStateType type) {
		this(climateState.getTemperature(), climateState.getHumidity(), type);
	}

	public ClimateState(float temperature, float humidity, ClimateStateType type) {
		this.type = type;
		this.temperature = type.clamp(temperature);
		this.humidity = type.clamp(humidity);
	}

	public ClimateState(NBTTagCompound compound, ClimateStateType type) {
		this.type = type;
		readFromNBT(compound);
	}

	public ClimateState(NBTTagCompound compound) {
		this.type = ClimateStateType.values()[compound.getByte(TYPE_NBT_KEY)];
		readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat(TEMPERATURE_NBT_KEY, temperature);
		compound.setFloat(HUMIDITY_NBT_KEY, humidity);
		compound.setByte(TYPE_NBT_KEY, (byte) type.ordinal());
		compound.setBoolean(ABSENT_NBT_KEY, !isPresent());
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.temperature = type.clamp(compound.getFloat(TEMPERATURE_NBT_KEY));
		this.humidity = type.clamp(compound.getFloat(HUMIDITY_NBT_KEY));
	}

	@Override
	public IClimateState copy(ClimateStateType type) {
		return new ClimateState(this, type);
	}

	@Override
	public IClimateState copy() {
		return new ClimateState(this, type);
	}

	@Override
	public IClimateState addTemperature(float temperature) {
		return ClimateStates.of(this.temperature + temperature, humidity, type);
	}

	@Override
	public IClimateState addHumidity(float humidity) {
		return ClimateStates.of(temperature, this.humidity + humidity, type);
	}

	@Override
	public IClimateState add(IClimateState state) {
		return ClimateStates.of(this.temperature + state.getTemperature(), this.humidity + state.getHumidity(), type);
	}

	@Override
	public IClimateState scale(double factor) {
		return ClimateStates.of((float) (this.temperature * factor), (float) (this.humidity * factor), type);
	}

	@Override
	public IClimateState remove(IClimateState state) {
		return ClimateStates.of(this.temperature - state.getTemperature(), this.humidity - state.getHumidity(), type);
	}

	@Override
	public boolean isPresent() {
		return !Float.isNaN(temperature) && !Float.isNaN(humidity);
	}

	@Override
	public ClimateStateType getType() {
		return type;
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	@Override
	public float getHumidity() {
		return humidity;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IClimateState)) {
			return false;
		}
		IClimateState otherState = (IClimateState) obj;
		return otherState.getTemperature() == temperature && otherState.getHumidity() == humidity;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(temperature) * 31 + Float.hashCode(humidity);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("temperature", temperature).add("humidity", humidity).toString();
	}
}