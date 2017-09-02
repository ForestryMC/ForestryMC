/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.util.math.BlockPos;

/**
 * A greenhouse chunk is used to save the greenhouse managers from the greenhouses that are located in the world chunk on this chunk position.
 */
public interface IGreenhouseChunk {
	/**
	 * @return a {@link Collection} with all greenhouse managers of the greenhouses that are located in the world chunk on this chunk position.
	 */
	Collection<IGreenhouseProvider> getProviders();

	/**
	 * Marks the manager, that has a {@link IGreenhouseBlock} on this position, as dirty.
	 */
	void markProviderDirty(BlockPos pos);

	/**
	 * @return A {@link java.util.Collection} with all managers that were modified in the last tick.
	 */
	Collection<IGreenhouseProvider> getDirtyProviders();

	/**
	 * Tries to get a {@link IGreenhouseBlock} from all managers that are located in this chunk.
	 *
	 * @return A {@link IGreenhouseBlock} if any manager has one block at this position, otherwise null.
	 */
	@Nullable
	IGreenhouseBlock get(BlockPos pos);

	/**
	 * Adds a manager to this chunk.
	 */
	void add(IGreenhouseProvider manager);

	/**
	 * Removed a manager from this chunk.
	 */
	void remove(IGreenhouseProvider manager);
}
