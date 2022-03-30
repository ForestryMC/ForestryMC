/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.util.LazyOptional;

public interface IClimateRoot {

	/**
	 * @return the listener at the given position in the given world if there is any.
	 */
	LazyOptional<IClimateListener> getListener(Level world, BlockPos pos);

	/**
	 * Can be used to get the climate state without the use of an {@link IClimateListener}.
	 *
	 * @return The climate state at the given location.
	 */
	IClimateState getState(Level world, BlockPos pos);

	/**
	 * @return The climate of the biome at the given position contained in a {@link IClimateState}.
	 */
	IClimateState getBiomeState(Level worldObj, BlockPos coordinates);

	/**
	 * @return Create a climate provider.
	 */
	IClimateProvider getDefaultClimate(Level world, BlockPos pos);

	/**
	 * @return The climate holder of the given world.
	 */
	IWorldClimateHolder getWorldClimate(Level world);
}
