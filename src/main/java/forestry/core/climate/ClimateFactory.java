package forestry.core.climate;

import forestry.api.greenhouse.IClimateHousing;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateFactory;
import forestry.greenhouse.climate.ClimateContainer;

public class ClimateFactory implements IClimateFactory{

	@Override
	public IClimateContainer createContainer(IClimateHousing climatedRegion) {
		return new ClimateContainer(climatedRegion);
	}

}
