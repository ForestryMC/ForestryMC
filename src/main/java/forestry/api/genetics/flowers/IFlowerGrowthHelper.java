/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics.flowers;

import java.util.Collection;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFlowerGrowthHelper {

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
    boolean plantRandomFlower(String flowerType, World world, BlockPos pos, Collection<BlockState> potentialFlowers);
}
