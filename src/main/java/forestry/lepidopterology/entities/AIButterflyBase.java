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
package forestry.lepidopterology.entities;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public abstract class AIButterflyBase extends Goal {

    protected final EntityButterfly entity;

    protected AIButterflyBase(EntityButterfly entity) {
        this.entity = entity;
    }

    @Nullable
    protected Vector3d getRandomDestination() {
        if (entity.isInWater()) {
            return getRandomDestinationUpwards();
        }

        Vector3d entityPos = entity.getPositionVec();
        Vector3d randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7, entityPos);

        if (randomTarget != null && validateDestination(randomTarget, false)) {
            return randomTarget;
        }
        return null;
    }

    @Nullable
    protected Vector3d getRandomDestinationUpwards() {
        Vector3d entityPos = entity.getPositionVec();
        Vector3d destination = entityPos.add(0, entity.getRNG().nextInt(10) + 2, 0);
        if (validateDestination(destination, true)) {
            return destination;
        } else {
            return null;
        }
    }

    private boolean validateDestination(Vector3d dest, boolean allowFluids) {
        if (dest.y < 1) {
            return false;
        }
        BlockPos pos = new BlockPos(dest);
        if (!entity.world.isBlockLoaded(pos)) {
            return false;
        }
        BlockState blockState = entity.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!allowFluids && blockState.getMaterial().isLiquid()) {
            return false;
        }
        //		if (!block.isPassable(entity.world, pos)) {
        if (!block.isAir(blockState, entity.world, pos)) {    //TODO
            return false;
        }
        return entity.getButterfly().isAcceptedEnvironment(entity.world, dest.x, dest.y, dest.z);
    }

}
