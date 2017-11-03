/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClimateManager {

	/**
	 * @deprecated Use {@link IClimateStates#create(float, float, ClimateStateType)}
	 */
	@Deprecated
	IClimateInfo createInfo(float temperature, float humidity);

	/**
	 * @deprecated Use {@link #getClimateState(World, BlockPos)}
	 */
	@Deprecated
	IClimateInfo getInfo(World world, BlockPos pos);

	/**
	 * Gets the current state of a container at this position or setSettings one with the datas from the biome.
	 * @since 5.3.4
	 */
	IClimateState getClimateState(World world, BlockPos pos);
	
	/**
	 * Creates a climate state with the help of the biome on this position.
	 * @since 5.3.4
	 */
	IClimateState getBiomeState(World world, BlockPos pos);

	/**
	 * @return Create a climate manager.
	 */
	IClimateProvider getDefaultClimate(World world, BlockPos pos);
}
