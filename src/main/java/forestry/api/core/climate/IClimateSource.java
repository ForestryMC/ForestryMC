/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core.climate;

import net.minecraft.util.math.BlockPos;

/**
 * A climate source is used to change the climate in a region.
 */
public interface IClimateSource {
	
	/**
	 * @param tickCount The current tick count.
	 * @param region The climate region, in that the source stands.
	 */
	void changeClimate(int tickCount, IClimateRegion region);
	
	BlockPos getPos();

}
