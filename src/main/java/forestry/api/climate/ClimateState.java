/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import com.google.common.base.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import forestry.api.core.INbtReadable;

import static forestry.api.climate.ImmutableClimateState.MAX;
import static forestry.api.climate.ImmutableClimateState.MIN;

/**
 * A {@link IClimateState} that has a range from 0.0F to 2.0F.
 */
public class ClimateState implements IClimateState, INbtReadable{
	
	private static final String TEMPERATURE_NBT_KEY = "TEMP";
	private static final String HUMIDITY_NBT_KEY = "HUMID";
	
	protected float temperature;
	protected float humidity;
	
	public ClimateState(float temperature, float humidity) {
		addTemperature(temperature);
		addHumidity(humidity);
	}
	
	public ClimateState(NBTTagCompound compound) {
		readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat(TEMPERATURE_NBT_KEY, temperature);
		compound.setFloat(HUMIDITY_NBT_KEY, humidity);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.temperature = compound.getFloat(TEMPERATURE_NBT_KEY);
		this.humidity = compound.getFloat(HUMIDITY_NBT_KEY);
	}
	
	public void setTemperature(float temperature) {
		this.temperature = MathHelper.clamp(temperature, MIN.temperature, MAX.temperature);
	}
	
	public void setHumidity(float humidity) {
		this.humidity = MathHelper.clamp(humidity, MIN.humidity, MAX.humidity);
	}
	
	@Override
	public ClimateState addTemperature(float temperature){
		setTemperature(this.temperature + temperature);
		return this;
	}
	
	@Override
	public ClimateState addHumidity(float humidity){
		setHumidity(this.humidity + humidity);
		return this;
	}
	
	@Override
	public ClimateState add(IClimateState state){
		addTemperature(state.getTemperature());
		addHumidity(state.getHumidity());
		return this;
	}
	
	@Override
	public ClimateState remove(IClimateState state){
		addTemperature(-state.getTemperature());
		addHumidity(-state.getHumidity());
		return this;
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
	public ImmutableClimateState toImmutable(){
		return new ImmutableClimateState(temperature, humidity);
	}
	
	@Override
	public ClimateState toMutable(){
		return this;
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
		return Float.hashCode(temperature) + Float.hashCode(humidity);
	}
	
    @Override
	public String toString() {
        return Objects.toStringHelper(this).add("temperature", temperature).add("humidity", humidity).toString();
    }

}
