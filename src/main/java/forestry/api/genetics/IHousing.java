/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.core.BlockPos;

/**
 * Any housing, hatchery or nest with a location in the world.
 */
//TODO: Remove in favor of ILocatable ?
public interface IHousing {

	/**
	 * The coordinates of the housing.
	 */
	BlockPos getCoordinates();

}
