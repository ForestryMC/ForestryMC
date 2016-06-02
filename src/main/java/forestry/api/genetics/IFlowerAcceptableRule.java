/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFlowerAcceptableRule {
	/**
	 * Checks if the flower at this position is accepted by this rule.
	 * flowerType is passed in case this rule is registered for multiple flower types.
	 */
	boolean isAcceptableFlower(IBlockState blockState, World world, BlockPos pos, String flowerType);
}
