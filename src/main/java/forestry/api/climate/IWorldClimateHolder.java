package forestry.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWorldClimateHolder {
	IClimateState getClimate(long position);

	int getRange(long position);

	boolean isCircular(long position);

	void addTransformer(long chunkPos, long transformerPos);

	void removeTransformer(long chunkPos, long transformerPos);

	void updateTransformer(IClimateTransformer transformer);

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

	long getLastUpdate(long chunkPos);

	boolean isPositionInTransformerRange(long position, Position2D blockPos);

	boolean hasTransformers(BlockPos pos);
}
