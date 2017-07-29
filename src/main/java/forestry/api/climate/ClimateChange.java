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

/**
 * A {@link IClimateState} that has a range from 0.0F to 2.0F and can be positiv an negativ.
 *
 *
 * It is mainly used by {@link IClimateSource} to change the {@link IClimateState} of the {@link IClimateContainer}.
 */
public class ClimateChange implements IClimateState, INbtReadable{
	
	private static final String TEMPERATURE_NBT_KEY = "TEMP";
	private static final String HUMIDITY_NBT_KEY = "HUMID";
	public static final ClimateChange ORIGIN = new ClimateChange(0.0F, 0.0F);
	
	
	public float temperature;
	public float humidity;
	
	public ClimateChange(float temperature, float humidity) {
		addTemperature(temperature);
		addHumidity(humidity);
	}
	
	public ClimateChange(NBTTagCompound compound) {
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
	
	@Override
	public float getTemperature() {
		return temperature;
	}
	
	@Override
	public float getHumidity() {
		return humidity;
	}
	
	public void setTemperature(float temperature) {
		this.temperature = 0;
		addTemperature(temperature);
	}
	
	public void setHumidity(float humidity) {
		this.humidity = 0;
		addHumidity(humidity);
	}
	
	@Override
	public ClimateChange add(IClimateState state){
		addTemperature(state.getTemperature());
		addHumidity(state.getHumidity());
		return this;
	}
	
	@Override
	public ClimateChange remove(IClimateState state){
		addTemperature(-state.getTemperature());
		addHumidity(-state.getHumidity());
		return this;
	}
	
	@Override
	public ClimateChange addTemperature(float temperature){
		this.temperature = MathHelper.clamp(this.temperature + temperature, -2.0F, 2.0F);
		return this;
	}
	
	@Override
	public ClimateChange addHumidity(float humidity){
		this.humidity = MathHelper.clamp(this.humidity + humidity, -2.0F, 2.0F);
		return this;
	}

	@Override
	public ImmutableClimateState toImmutable() {
		return new ImmutableClimateState(temperature, humidity);
	}

	@Override
	public ClimateState toMutable() {
		return new ClimateState(temperature, humidity);
	}
	
    @Override
	public String toString() {
        return Objects.toStringHelper(this).add("temperature", temperature).add("humidity", humidity).toString();
    }
	
}
