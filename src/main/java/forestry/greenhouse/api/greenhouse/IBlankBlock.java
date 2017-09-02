/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

public interface IBlankBlock extends IGreenhouseBlock {

	@Override
	@Nullable
	IBlankBlock getRoot();

	/**
	 * @return test if a face was already tested, false if not.
	 */
	boolean isFaceTested(EnumFacing facing);

	void setFaceTested(EnumFacing facing, boolean isTested);

	/**
	 * @return if this block has a {@link IWallBlock} next to him.
	 */
	boolean isNearWall();

	void setNearWall(boolean nearWall);

	/**
	 * Called after the block is created.
	 */
	void validate();

	/**
	 * Called before the block will be removed.
	 */
	void invalidate(boolean chunkUnloading);

	boolean isValid();

}
