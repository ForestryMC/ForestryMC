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

public interface IFlowerGrowthHelper {
	/**
	 * Plants a random flower from flowerType at the specified position.
	 *
	 * @return true if successful, false if it could not be planted.
	 * @deprecated since Forestry 5.5.4. Use {@link #plantRandomFlower(String, World, BlockPos, Collection)}
	 */
	@Deprecated
	boolean plantRandomFlower(String flowerType, World world, BlockPos pos);

	/**
	 * Plants a random flower from flowerType at the specified position.
	 *
	 * @param flowerType       the flower type that can be planted
	 * @param world            the world to plant flowers in
	 * @param pos              the position to plant the flowers
	 * @param potentialFlowers the potential flowers that can be planted
	 * @return true if a flower was planted, false otherwise
	 * @since Forestry 5.5.4
	 */
	boolean plantRandomFlower(String flowerType, World world, BlockPos pos, Collection<IBlockState> potentialFlowers);
}
