package forestry.core.climate;

import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateFactory;
import forestry.api.greenhouse.IClimateHousing;

public class ClimateFactory implements IClimateFactory{

	@Override
	public IClimateContainer createContainer(IClimateHousing climatedRegion) {
		return new ClimateContainer(climatedRegion);
	}

}
