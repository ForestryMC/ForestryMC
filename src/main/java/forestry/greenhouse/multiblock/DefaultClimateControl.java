package forestry.greenhouse.multiblock;

import forestry.api.climate.IClimateControl;

public class DefaultClimateControl implements IClimateControl {

	public static final DefaultClimateControl instance = new DefaultClimateControl();
	
	public DefaultClimateControl() {
	}
	
	@Override
	public float getControlTemperature() {
		return 2.0F;
	}

	@Override
	public float getControlHumidity() {
		return 2.0F;
	}

	@Override
	public void setControlTemperature(float temperature) {		
	}

	@Override
	public void setControlHumidity(float humidity) {
	}

}
