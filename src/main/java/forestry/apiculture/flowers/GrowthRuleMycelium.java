/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture.flowers;

import forestry.api.genetics.flowers.IFlowerGrowthHelper;
import forestry.api.genetics.flowers.IFlowerGrowthRule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;

public class GrowthRuleMycelium implements IFlowerGrowthRule {

    @Override
    public boolean growFlower(
            IFlowerGrowthHelper helper,
            String flowerType,
            ServerWorld world,
            BlockPos pos,
            Collection<BlockState> potentialFlowers
    ) {
        return isValidSpot(world, pos) &&
               helper.plantRandomFlower(flowerType, world, pos, potentialFlowers);
    }

    private boolean isValidSpot(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos) || !world.isAirBlock(pos)) {
            return false;
        }

        Block ground = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock();
        return ground == Blocks.MYCELIUM;
    }

}
