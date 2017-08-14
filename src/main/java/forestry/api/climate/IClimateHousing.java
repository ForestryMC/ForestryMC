/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateHousing {

	/**
	 * @return The climate container of this region.
	 */
	IClimateContainer getClimateContainer();
	
	/**
	 * @return The size of the region in blocks.
	 */
	int getSize();
	
	void onUpdateClimate();
	
	/**
	 * @return The default climate state. It is calculated out of all biomedata that this region contains.
	 */
	ImmutableClimateState getDefaultClimate();
	
	/**
	 * Must not be named "getWorld" to avoid SpecialSource issue https://github.com/md-5/SpecialSource/issues/12
	 *
	 * @return The position at that the housing is.
	 */
	BlockPos getCoordinates();
	
	/**
	 * Must not be named "getWorld" to avoid SpecialSource issue https://github.com/md-5/SpecialSource/issues/12
	 *
	 * @return The world in that the housing is.
	 */
	World getWorldObj();
	
}
