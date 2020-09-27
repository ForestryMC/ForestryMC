/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.flowers;

import forestry.api.genetics.flowers.IFlowerGrowthHelper;
import forestry.api.genetics.flowers.IFlowerGrowthRule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GrowthRuleFertilize implements IFlowerGrowthRule {

    private final List<Block> allowedItems;

    public GrowthRuleFertilize(Block... allowedItems) {
        this.allowedItems = Arrays.asList(allowedItems);
    }

    @Override
    public boolean growFlower(
            IFlowerGrowthHelper helper,
            String flowerType,
            ServerWorld world,
            BlockPos pos,
            Collection<BlockState> potentialFlowers
    ) {
        return growFlower(world, pos);
    }

    private boolean growFlower(ServerWorld world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block ground = state.getBlock();
        for (Block b : this.allowedItems) {
            if (b == ground && b instanceof IGrowable) {
                IGrowable growable = (IGrowable) b;
                if (growable.canGrow(world, pos, state, false)) {//TODO what to put for isClient
                    for (int i = 0; i < world.rand.nextInt(2) + 1; i++) {
                        growable.grow(world, world.rand, pos, state);
                    }
                }
            }
        }

        return false;
    }

}
