package forestry.core.climate;

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateFactory;
import forestry.api.climate.IClimateHousing;

public class ClimateFactory implements IClimateFactory{

	@Override
	public IClimateContainer createContainer(IClimateHousing climatedRegion) {
		return new ClimateContainer(climatedRegion);
	}

}
