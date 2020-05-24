/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import net.minecraft.util.math.BlockPos;

/**
 * Cache that can be used to cache the last extends of {@link IFarmLogic}s. Some {@link IFarmLogic}s that potentially
 * could make many operation for a single position like the arboreal logic can use this to cache the last position they
 * worked on.
 */
public interface IExtentCache {

	/**
	 * Gets the current extent for the given direction and position.
	 *
	 * @param direction The direction of the farm logic.
	 * @param pos       The position the logic starts to operate on
	 * @return The current extent for the given direction and position.
	 */
	int getExtents(FarmDirection direction, BlockPos pos);

	/**
	 * Sets the current extent for the given direction and position.
	 *
	 * @param direction The direction of the farm logic.
	 * @param pos       The position the logic starts to operate on
	 * @param extend    The extent
	 */
	void setExtents(FarmDirection direction, BlockPos pos, int extend);

	/**
	 * Clears the cache for the given direction.
	 * <p>
	 * For example used if the player remove the circuit from the farm.
	 *
	 * @param direction The direction that should be cleared
	 */
	void cleanExtents(FarmDirection direction);

	/**
	 * Gets the current extend but returns 0 if the extend is bigger given maximal extent
	 *
	 * @param direction The direction of the farm logic.
	 * @param pos       The position the logic starts to operate on
	 * @param maxExtend The maximal extent this method can return. If the current extend is bigger than this value 0
	 *                  will be returned.
	 * @return The current extend but returns 0 if the extend is bigger given maximal extent.
	 */
	default int getValidExtent(FarmDirection direction, BlockPos pos, int maxExtend) {
		int lastExtents = getExtents(direction, pos);
		if (lastExtents > maxExtend) {
			lastExtents = 0;
		}
		return lastExtents;
	}

	/**
	 * Offsets the given base location in the given direction by the valid extent of the logic position.
	 *
	 * @param direction    The direction of the farm logic.
	 * @param pos          The position the logic starts to operate on
	 * @param maxExtend    The maximal extent as defined in "getValidExtent"
	 * @param baseLocation The location that will be offset by the extent
	 * @return The base location offset in the given direction by the valid extent of the logic position.
	 */
	default BlockPos getValidPosition(FarmDirection direction, BlockPos pos, int maxExtend, BlockPos baseLocation) {
		int extent = getValidExtent(direction, pos, maxExtend);
		return baseLocation.offset(direction.getFacing(), extent);
	}

	/**
	 * Increases the valid position by one.
	 *
	 * @param direction The direction of the farm logic.
	 * @param pos       The position the logic starts to operate on
	 * @param maxExtend The maximal extent as defined in "getValidExtent"
	 * @return The valid position increased by one.
	 */
	default int increaseExtent(FarmDirection direction, BlockPos pos, int maxExtend) {
		int validExtent = getValidExtent(direction, pos, maxExtend);
		setExtents(direction, pos, ++validExtent);
		return validExtent;
	}
}
