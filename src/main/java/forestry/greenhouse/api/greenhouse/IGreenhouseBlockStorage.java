/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;

public interface IGreenhouseBlockStorage {
	/**
	 * @return a {@link IGreenhouseBlock} if there is one on this position.
	 */
	@Nullable
	IGreenhouseBlock getBlock(BlockPos pos);

	/**
	 * Sets a {@link IGreenhouseBlock} to a position in the world.
	 */
	boolean setBlock(BlockPos pos, @Nullable IGreenhouseBlock block);

	/**
	 * @return The number of {@link IBlankBlock}s that this provider contains.
	 */
	int getBlockCount();

	/**
	 * Removes a {@link IGreenhouseBlock} from this provider and add its neighbor blocks to the set.
	 */
	<B extends IGreenhouseBlock> void removeBlock(IGreenhouseBlock block);

	IGreenhouseProvider getProvider();
}
