/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface provides some methods to add or remove {@link IClimateTransformer}s and to getComb the current climate
 * state at a given position.
 * <p>
 * You can use {@link IClimateRoot#getWorldClimate(World)} to getComb the instance of this interface that provides the
 * information about a given world.
 */
public interface IWorldClimateHolder {
	/**
	 * @return The cached climate state of the transformer at the given position.
	 */
	IClimateState getClimate(long position);

	/**
	 * @return The cached range of the transformer at the given position.
	 */
	int getRange(long position);

	/**
	 * @return The cached circular state of the transformer at the given position.
	 */
	boolean isCircular(long position);

	/**
	 * Adds the transformer to the chunk at the given chunkPos.
	 */
	void addTransformer(long chunkPos, long transformerPos);

	/**
	 * Removes the transformer from the chunk at the given chunkPos.
	 */
	void removeTransformer(long chunkPos, long transformerPos);

	/**
	 * Updates the cached climate state.
	 */
	void updateTransformer(IClimateTransformer transformer);

	/**
	 * Removes the transformer from
	 */
	void removeTransformer(IClimateTransformer transformer);

	/**
	 * @return The difference between the climate of the biome at the given position and climate of the transformers
	 * that are in range of this location.
	 */
	IClimateState getState(BlockPos pos);

	/**
	 * @return the {@link World#getTotalWorldTime()} at the moment the last transformer changed its
	 * {@link IClimateTransformer#getCurrent()} state or a {@link IClimateTransformer} has been removed or added to the
	 * holder.
	 */
	long getLastUpdate(BlockPos pos);

	/**
	 * @return The time of last update of the chunk at the given position.
	 */
	long getLastUpdate(long chunkPos);

	/**
	 * @return If the given {@link Position2D} is in the range of the transformer at the given position.
	 */
	boolean isPositionInTransformerRange(long position, Position2D blockPos);

	/**
	 * @return If the chunk at the given {@link BlockPos} has any transformers.
	 */
	boolean hasTransformers(BlockPos pos);
}
