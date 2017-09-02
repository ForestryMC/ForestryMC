/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IGreenhouseBlock {

	/**
	 * @return the position of this block.
	 */
	BlockPos getPos();

	/**
	 * @return the root block of this block.
	 */
	@Nullable
	IGreenhouseBlock getRoot();

	/**
	 * @return the side of this block that shows to the root block.
	 */
	@Nullable
	EnumFacing getRootFace();

	/**
	 * @return the manager of this block.
	 */
	IGreenhouseProvider getProvider();

	/**
	 * Called if a neighbor block of this block is removed.
	 */
	void onNeighborRemoved(IBlankBlock changedBlock, EnumFacing facing, boolean forcedRemove, Set<IGreenhouseBlock> blocksToCheck);

	IGreenhouseBlockHandler getHandler();

}
