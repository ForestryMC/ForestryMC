package forestry.core.climate;

import java.util.LinkedHashMap;
import java.util.Map;

import forestry.api.climate.ClimateType;
import forestry.greenhouse.api.climate.IClimateData;

public class ClimateData implements IClimateData {
	private final Map<String, Float> temperatureData = new LinkedHashMap<>();
	private final Map<String, Float> humidityData = new LinkedHashMap<>();

	@Override
	public ClimateData addData(ClimateType type, String displayName, float value) {
		if(type == ClimateType.HUMIDITY){
			humidityData.put(displayName, value);
		}else{
			temperatureData.put(displayName, value);
		}
		return this;
	}

	public Map<String, Float> getData(ClimateType type){
		if(type == ClimateType.HUMIDITY){
			return humidityData;
		}
		return temperatureData;
	}
}
