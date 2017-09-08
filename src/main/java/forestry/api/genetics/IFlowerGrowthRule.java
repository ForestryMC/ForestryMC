/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Basic condition for flower growing, such as checking that the soil is the correct type.
 */
public interface IFlowerGrowthRule {
	/**
	 * Checks a position for suitability, and then plants a flower there.
	 * Returns true on success.
	 * For implementers, you can plant a random flower using IFlowerGrowthHelper.plantRandomFlower
	 *
	 * @since Forestry 4.0.8
	 * @deprecated since Forestry 5.5.4. Use {@link #growFlower(IFlowerGrowthHelper, String, World, BlockPos, Collection)}
	 */
	@Deprecated
	boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, BlockPos pos);

	/**
	 * Checks a position for suitability, and then plants a flower there.
	 * Returns true on success.
	 * For implementers, you can plant a random flower using IFlowerGrowthHelper.plantRandomFlower
	 *
	 * @since Forestry 5.5.4
	 */
	default boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, BlockPos pos, Collection<IBlockState> potentialFlowers) {
		//noinspection deprecation
		return growFlower(helper, flowerType, world, pos);
	}
}
