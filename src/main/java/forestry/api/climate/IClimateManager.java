/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.greenhouse.Position2D;

public interface IClimateManager {
	
	/**
	 * @return The current state of a container at this position if one exists.
	 * @since 5.3.4
	 */
	@Nullable
	IClimateContainer getContainer(World world, BlockPos pos);
	
	/**
	 * Gets the current state of a container at this position or setSettings one with the datas from the biome.
	 * @since 5.3.4
	 */
	IClimateState getClimateState(World world, BlockPos pos);
	
	/**
	 * Creates a climate state with the help of the biome on this position.
	 */
	ImmutableClimateState getBiomeState(World world, BlockPos pos);

	/**
	 * @return Create a climate manager.
	 */
	IClimateProvider getDefaultClimate(World world, BlockPos pos);
	
	void addSource(IClimateSourceOwner owner);
	
	void removeSource(IClimateSourceOwner owner);

	Collection<IClimateSourceOwner> getSources(World world, Position2D position);

}
