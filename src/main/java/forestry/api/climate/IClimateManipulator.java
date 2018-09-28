/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

public interface IClimateManipulator {

	IClimateState getCurrent();

	IClimateState getStart();

	IClimateState getTarget();

	boolean allowsBackwards();

	IClimateState getDefault();

	ClimateType getType();

	IClimateState addChange(boolean simulated);

	IClimateState removeChange(boolean simulated);

	boolean canAdd();

	void finish();
}
