/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface IGreenhouseBlockManager {

	/**
	 * @return the {@link IGreenhouseBlockHandler} that is used to handle blank blocks in the {@link IGreenhouseProvider}
	 */
	IGreenhouseBlockHandler<IBlankBlock, IBlankBlock> getBlankBlockHandler();

	/**
	 * @return the {@link IGreenhouseBlockHandler} that is used to handle wall blocks in the {@link IGreenhouseProvider}
	 */
	IGreenhouseBlockHandler<IWallBlock, IBlankBlock> getWallBlockHandler();

	/**
	 * Tries to get a {@link IGreenhouseBlock} from all managers that are located at the chunk of the pos.
	 *
	 * @return a {@link IGreenhouseBlock} if any manager of the chunk at this position has one block at this position, otherwise null.
	 */
	@Nullable
	IGreenhouseBlock getBlock(World world, BlockPos pos);

	/**
	 * @return the chunk that is at this position.
	 */
	@Nullable
	IGreenhouseChunk getChunk(World world, long chunkPos);

	/**
	 * @return the chunk that is at this position or if there is no it creates one.
	 */
	IGreenhouseChunk getOrCreateChunk(World world, long chunkPos);

	/**
	 * @return creates a chunk and add it at this position
	 */
	IGreenhouseChunk createChunk(World world, long chunkPos);

	default IGreenhouseChunk getChunk(World world, int xPos, int yPos) {
		return getChunk(world, ChunkPos.asLong(xPos, yPos));
	}

	default IGreenhouseChunk getOrCreateChunk(World world, int xPos, int yPos) {
		return getOrCreateChunk(world, ChunkPos.asLong(xPos, yPos));
	}

	default IGreenhouseChunk createChunk(World world, int xPos, int yPos) {
		return createChunk(world, ChunkPos.asLong(xPos, yPos));
	}

	@Nullable
	void markChunkDirty(World world, long pos);

	@Nullable
	List<Long> getDirtyChunks(World world);

	@Nullable
	void markBlockDirty(World world, BlockPos pos);

	void markProviderDirty(World world, BlockPos pos, IGreenhouseProvider provider);
}
