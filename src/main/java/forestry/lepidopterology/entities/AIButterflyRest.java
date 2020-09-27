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

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.IPlantable;

import java.util.EnumSet;

public class AIButterflyRest extends AIButterflyBase {

    public AIButterflyRest(EntityButterfly entity) {
        super(entity);
        setMutexFlags(EnumSet.of(Flag.MOVE));
        //		setMutexBits(3);	TODO mutex
    }

    @Override
    public boolean shouldExecute() {

        if (entity.getExhaustion() < EntityButterfly.EXHAUSTION_REST
            && entity.canFly()) {
            return false;
        }

        Vector3d entityPos = entity.getPositionVec();
        int x = (int) entityPos.x;
        int y = (int) Math.floor(entityPos.y);
        int z = (int) entityPos.z;
        BlockPos pos = new BlockPos(x, y, z);

        if (!canLand(pos)) {
            return false;
        }

        pos = pos.add(x, -1, z);
        if (entity.world.isAirBlock(pos)) {
            return false;
        }
        BlockState blockState = entity.world.getBlockState(pos);
        if (blockState.getMaterial().isLiquid()) {
            return false;
        }
        if (!entity.getButterfly().isAcceptedEnvironment(entity.world, x, pos.getY(), z)) {
            return false;
        }

        entity.setDestination(null);
        entity.setState(EnumButterflyState.RESTING);
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (entity.getExhaustion() <= 0 && entity.canFly()) {
            return false;
        }
        return !entity.isInWater();
    }

    @Override
    public void startExecuting() {
    }

    @Override
    public void resetTask() {
    }

    @Override
    public void tick() {
        entity.changeExhaustion(-1);
    }

    private boolean canLand(BlockPos pos) {
        if (!entity.world.isBlockLoaded(pos)) {
            return false;
        }
        BlockState blockState = entity.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!block.isAir(blockState, entity.world, pos)) {    //TODO
            //			if (!block.isPassable(entity.world, pos)) {
            return false;
        }
        if (isPlant(blockState)) {
            return true;
        }

        BlockState blockStateBelow = entity.world.getBlockState(pos.down());
        Block blockBelow = blockStateBelow.getBlock();
        return isRest(blockBelow) || blockBelow.isIn(BlockTags.LEAVES);
    }

    private static boolean isRest(Block block) {
        if (block instanceof FenceBlock) {
            return true;
        }
        return block instanceof WallBlock;
    }

    private static boolean isPlant(BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof FlowerBlock) {
            return true;
        } else if (block instanceof IPlantable) {
            return true;
        } else if (block instanceof IGrowable) {
            return true;
        } else {
            return blockState.getMaterial() == Material.PLANTS;
        }
    }
}
