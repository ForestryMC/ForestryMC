/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.core.climate;

import com.google.common.base.MoreObjects;

import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimateState;

public class ClimateState implements IClimateState, IClimateInfo {

	// The minimum climate state.
	public static final ClimateState MIN = new ClimateState(0.0F, 0.0F, ClimateStateType.IMMUTABLE);
	// The maximum climate state.
	public static final ClimateState MAX = new ClimateState(2.0F, 2.0F, ClimateStateType.IMMUTABLE);

	public static final String TEMPERATURE_NBT_KEY = "TEMP";
	public static final String HUMIDITY_NBT_KEY = "HUMID";
	public static final String TYPE_NBT_KEY = "TYPE";
	public static final String ABSENT_NBT_KEY = "ABSENT";

	protected final ClimateStateType type;
	protected final Function<Float, Float> bounds;
	protected float temperature;
	protected float humidity;

	public ClimateState(IClimateState climateState, ClimateStateType type) {
		this(climateState.getTemperature(), climateState.getHumidity(), type);
	}

	public ClimateState(float temperature, float humidity, ClimateStateType type) {
		addTemperature(temperature);
		addHumidity(humidity);
		this.type = type;
		this.bounds = type.bounds;
	}

	public ClimateState(NBTTagCompound compound, ClimateStateType type) {
		this.type = type;
		this.bounds = type.bounds;
		readFromNBT(compound);
	}

	public ClimateState(NBTTagCompound compound) {
		this.type = ClimateStateType.values()[compound.getByte(TYPE_NBT_KEY)];
		this.bounds = type.bounds;
		readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat(TEMPERATURE_NBT_KEY, temperature);
		compound.setFloat(HUMIDITY_NBT_KEY, humidity);
		compound.setByte(TYPE_NBT_KEY, (byte)type.ordinal());
		compound.setBoolean(ABSENT_NBT_KEY, !isPresent());
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.temperature = compound.getFloat(TEMPERATURE_NBT_KEY);
		this.humidity = compound.getFloat(HUMIDITY_NBT_KEY);
	}

	@Override
	public IClimateState setHumidity(float humidity) {
		if(type == ClimateStateType.IMMUTABLE){
			return new ClimateState(getTemperature(), humidity, ClimateStateType.IMMUTABLE);
		}
		this.humidity = bounds.apply(humidity);
		if(!isPresent()){
			return AbsentClimateState.INSTANCE;
		}
		return this;
	}

	@Override
	public IClimateState setTemperature(float temperature) {
		if(type == ClimateStateType.IMMUTABLE){
			return new ClimateState(temperature, getHumidity(), ClimateStateType.IMMUTABLE);
		}
		this.temperature = bounds.apply(temperature);
		if(!isPresent()){
			return AbsentClimateState.INSTANCE;
		}
		return this;
	}

	@Override
	public IClimateState toImmutable() {
		return new ClimateState(this, ClimateStateType.IMMUTABLE);
	}

	@Override
	public IClimateState toMutable() {
		return new ClimateState(this, ClimateStateType.MUTABLE);
	}

	@Override
	public IClimateState toChange() {
		return new ClimateState(this, ClimateStateType.CHANGE);
	}

	@Override
	public IClimateState addTemperature(float temperature){
		this.temperature=+ temperature;
		if(!isPresent()){
			return AbsentClimateState.INSTANCE;
		}
		return this;
	}
	
	@Override
	public IClimateState addHumidity(float humidity){
		this.humidity=+ humidity;
		if(!isPresent()){
			return AbsentClimateState.INSTANCE;
		}
		return this;
	}
	
	@Override
	public IClimateState add(IClimateState state){
		addTemperature(state.getTemperature());
		addHumidity(state.getHumidity());
		if(!isPresent()){
			return AbsentClimateState.INSTANCE;
		}
		return this;
	}
	
	@Override
	public IClimateState remove(IClimateState state){
		addTemperature(-state.getTemperature());
		addHumidity(-state.getHumidity());
		if(!isPresent()){
			return AbsentClimateState.INSTANCE;
		}
		return this;
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
		if(!(obj instanceof IClimateState)){
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