/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorState;
import forestry.greenhouse.api.climate.GreenhouseState;
import forestry.greenhouse.api.climate.IClimateContainer;

/**
 * Can be used to test if a space is closed. Is used by the greenhouse.
 */
public interface IGreenhouseProvider extends IErrorLogicSource {

	/**
	 * @return the climate container of this provider.
	 */
	@Nullable
	IClimateContainer getClimateContainer();

	/**
	 * Adds a listener to this provider.
	 */
	void addListener(IGreenhouseProviderListener listener);

	/**
	 * @return All listeners that this provider has.
	 */
	Collection<IGreenhouseProviderListener> getListeners();

	/**
	 * @param centerPos The position at that the provider starts to test.
	 */
	void init(BlockPos centerPos, IGreenhouseLimits limits);

	/**
	 * Invalidate and delete all {@link IGreenhouseBlock}s of this provider.
	 */
	void clear(boolean chunkUnloading);

	/**
	 * Called from the thread if a {@link IGreenhouseBlock} was modified by the player.
	 */
	void recreate();

	void onUnloadChunk(long chunkPos);

	void onLoadChunk(long chunkPos);

	/**
	 * @return True if the provider waits for a chunk to be loaded, before he can test if it is closed.
	 */
	boolean hasUnloadedChunks();

	/**
	 * @return The position from that the provider tests if it is closed.
	 */
	BlockPos getCenterPos();

	/**
	 * @return The limits that a greenhouse has. Used to test if the greenhouse is tested.
	 * At the test the provider stops if the currently tested position is out of this limits and marks the greenhouse as open.
	 * Null if the provider is not created.
	 */
	@Nullable
	IGreenhouseLimits getLimits();

	/**
	 * Null if the provider is not created.
	 */
	@Nullable
	IGreenhouseLimits getUsedLimits();

	GreenhouseState getState();

	World getWorld();

	/**
	 * @return true if the space is closed.
	 */
	boolean isClosed();

	/**
	 * Test if the provider is closed.
	 */
	IErrorState checkPosition(BlockPos position);

	int getSize();

	/**
	 * @return All {@link IGreenhouseBlockHandler}s that this provider uses.
	 */
	Collection<IGreenhouseBlockHandler> getHandlers();

	IGreenhouseBlockStorage getStorage();

	void onBlockChange();

	void scheduledUpdate();
}
