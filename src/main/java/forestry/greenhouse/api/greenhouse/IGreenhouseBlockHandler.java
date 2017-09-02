/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import forestry.api.core.IErrorState;

public interface IGreenhouseBlockHandler<B extends IGreenhouseBlock, R extends IGreenhouseBlock> {

	void onRemoveBlock(IGreenhouseBlockStorage storage, B blockToRemove);

	B createBlock(IGreenhouseBlockStorage storage, @Nullable R root, @Nullable EnumFacing rootFacing, @Nullable BlockPos position);

	B getBlock(IGreenhouseBlockStorage storage, BlockPos position);

	@Nullable
	IErrorState checkNeighborBlocks(IGreenhouseBlockStorage storage, B blockToCheck, List<IGreenhouseBlock> newBlocksToCheck);

	boolean onCheckPosition(IGreenhouseBlockStorage storage, R rootBlock, BlockPos position, EnumFacing facing, IGreenhouseBlock block, List<IGreenhouseBlock> newBlocksToCheck);

	Class<? extends B> getBlockClass();
}
