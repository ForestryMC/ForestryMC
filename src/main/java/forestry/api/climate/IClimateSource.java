/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import javax.annotation.Nullable;

/**
 * A climate source is used to change the climate in a region.
 */
public interface IClimateSource {

	/**
	 * @param tickCount The current tick count.
	 * @param region    The climate region, in that the source stands.
	 * @return Return true if the the climate has changed.
	 */
	boolean changeClimate(int tickCount, IClimateRegion region);

	/**
	 * @return The ticks that are required for one change.
	 */
	int getTicksForChange(IClimateRegion region);

	@Nullable
	IClimateSourceProvider getProvider();

}
