/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics.flowers;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IFlowerAcceptableRule {
	/**
	 * Checks if the flower at this position is accepted by this rule.
	 * flowerType is passed in case this rule is registered for multiple flower types.
	 */
	boolean isAcceptableFlower(BlockState blockState, Level world, BlockPos pos, String flowerType);
}
