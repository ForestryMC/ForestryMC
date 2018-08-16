/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate.source;

import forestry.api.circuits.ICircuit;
import forestry.api.climate.ClimateType;

public interface IClimateSourceCircuitable<P extends IClimateSourceProxy> extends IClimateSource<P> {
	/**
	 * Called by the {@link IClimateSourceProxy} if its {@link ICircuit} changes.
	 */
	void changeSourceConfig(ClimateType type, float changeChange, float rangeChange, float energyChange);
}
