/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.genetics.flowers;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;

/**
 * Basic condition for flower growing, such as checking that the soil is the correct type.
 */
public interface IFlowerGrowthRule {
    /**
     * Checks a position for suitability, and then plants a flower there.
     * Returns true on success.
     * For implementers, you can plant a random flower using IFlowerGrowthHelper.plantRandomFlower
     *
     * @since Forestry 5.5.4
     */
    boolean growFlower(
            IFlowerGrowthHelper helper,
            String flowerType,
            ServerWorld world,
            BlockPos pos,
            Collection<BlockState> potentialFlowers
    );
}
