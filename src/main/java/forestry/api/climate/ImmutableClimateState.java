/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import com.google.common.base.MoreObjects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import forestry.api.core.INbtWritable;

/**
 * A {@link IClimateState} that has a range from 0.0F to 2.0F and can not be changed.
 */
public class ImmutableClimateState implements IClimateState, INbtWritable{

	// The minimum climate state.
	public static final ImmutableClimateState MIN = new ImmutableClimateState(0.0F, 0.0F);
	// The climate state of a plain biome.
	public static final ImmutableClimateState PLAIN = new ImmutableClimateState(0.8F, 0.4F);
	// The maximum climate state.
	public static final ImmutableClimateState MAX = new ImmutableClimateState(2.0F, 2.0F);
	
	private static final String TEMPERATURE_NBT_KEY = "TEMP";
	private static final String HUMIDITY_NBT_KEY = "HUMID";
	
	protected final float temperature;
	protected final float humidity;
	
	public ImmutableClimateState(float temperature, float humidity) {
		this.temperature = MathHelper.clamp(temperature, 0.0F, 2.0F);
		this.humidity = MathHelper.clamp(humidity, 0.0F, 2.0F);
		
	}
	
	public ImmutableClimateState(NBTTagCompound compound) {
		float temperature = compound.getFloat(TEMPERATURE_NBT_KEY);
		float humidity = compound.getFloat(HUMIDITY_NBT_KEY);
		this.temperature = MathHelper.clamp(temperature, 0.0F, 2.0F);
		this.humidity = MathHelper.clamp(humidity, 0.0F, 2.0F);
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat(TEMPERATURE_NBT_KEY, temperature);
		compound.setFloat(HUMIDITY_NBT_KEY, humidity);
		return compound;
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
	public ImmutableClimateState addTemperature(float temperature){
		return new ImmutableClimateState(this.temperature + temperature, humidity);
	}
	
	@Override
	public ImmutableClimateState addHumidity(float humidity){
		return new ImmutableClimateState(this.temperature, this.humidity + humidity);
	}
	
	@Override
	public ImmutableClimateState remove(IClimateState state) {
		return new ImmutableClimateState(temperature - state.getTemperature(), humidity - state.getHumidity());
	}
	
	@Override
	public ImmutableClimateState add(IClimateState state) {
		return new ImmutableClimateState(temperature + state.getTemperature(), humidity + state.getHumidity());
	}
	
	@Override
	public ImmutableClimateState toImmutable(){
		return this;
	}
	
	@Override
	public ClimateState toMutable(){
		return new ClimateState(temperature, humidity);
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
        return MoreObjects.toStringHelper(this).add("temperature", temperature).add("humidity", humidity).toString();
    }

}
