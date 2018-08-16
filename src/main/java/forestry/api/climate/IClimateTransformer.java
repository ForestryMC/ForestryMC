/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import forestry.api.core.ILocatable;

public interface IClimateTransformer extends ILocatable {
	int getRange();

	IClimateState getTarget();

	IClimateState getCurrent();

	IClimateState getDefault();

	void setCircular(boolean circular);

	boolean isCircular();

	void setRange(int range);
}
