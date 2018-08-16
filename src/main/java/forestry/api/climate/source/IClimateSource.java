/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate.source;

import forestry.api.climate.IClimateLogic;
import forestry.api.climate.IClimateState;
import forestry.api.climate.LogicInfo;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

/**
 * A climate source is stored in a {@link IClimateSourceProxy}. It is used by the {@link IClimateSourceLogic}s to change the climate of a {@link IClimateLogic}.
 * One {@link IClimateSource} can only be used by one {@link IClimateSourceLogic} at the same time.
 */
public interface IClimateSource<P extends IClimateSourceProxy> extends INbtWritable, INbtReadable {

	/**
	 * @param info
	 */
	void doWork(LogicInfo info);

	/**
	 * @return true if source has changed the climate at the last work circle.
	 */
	boolean isActive();

	/**
	 * @return A copy of the current state of this source.
	 */
	IClimateState getState();

	P getProxy();

}
