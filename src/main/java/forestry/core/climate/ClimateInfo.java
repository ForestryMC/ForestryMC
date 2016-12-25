package forestry.core.climate;

import java.io.IOException;
import forestry.api.climate.IClimateInfo;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.nbt.NBTTagCompound;

public class ClimateInfo implements IClimateInfo{

	public static final ClimateInfo MIN = new ClimateInfo(false);
	public static final ClimateInfo MAX = new ClimateInfo(true);
	
	private final float temperature;
	private final float humidity;
	
	private ClimateInfo(boolean isMax) {
		if(isMax){
			this.temperature = 2.0F;
			this.humidity = 2.0F;
		}else{
			this.temperature = 0.0F;
			this.humidity = 0.0F;
		}
	}
	
	public ClimateInfo(float temperature, float humidity) {
		if (temperature > MAX.getTemperature()) {
			temperature = MAX.getTemperature();
		}
		if (humidity > MAX.getHumidity()) {
			humidity = MAX.getHumidity();
		}
		if (temperature < MIN.getTemperature()) {
			temperature = MIN.getTemperature();
		}
		if (humidity < MIN.getHumidity()) {
			humidity = MIN.getHumidity();
		}
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	public ClimateInfo(NBTTagCompound compound) {
		this.temperature = compound.getFloat("Temperature");
		this.humidity = compound.getFloat("Humidity");
	}
	
	public ClimateInfo(PacketBufferForestry data) throws IOException {
		this.temperature = data.readFloat();
		this.humidity = data.readFloat();
	}
	
	@Override
	public float getTemperature() {
		return temperature;
	}

	@Override
	public float getHumidity() {
		return humidity;
	}

}
