/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.greenhouse.api.greenhouse.Position2D;

public interface IGreenhouseClimateManager {
	/**
	 * @return The current state of a container at this position if one exists.
	 */
	@Nullable
	IClimateContainer getContainer(World world, BlockPos pos);

	void addSource(IClimateSourceOwner owner);

	void removeSource(IClimateSourceOwner owner);

	Collection<IClimateSourceOwner> getSources(World world, Position2D position);

	void registerModifier(IClimateModifier modifier);

	Collection<IClimateModifier> getModifiers();
}
