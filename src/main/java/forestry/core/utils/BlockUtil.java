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
package forestry.core.utils;

import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;


public abstract class BlockUtil {


    public static boolean alwaysTrue(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    public static boolean alwaysFalse(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    public static List<ItemStack> getBlockDrops(IWorld world, BlockPos posBlock) {
        BlockState blockState = world.getBlockState(posBlock);

        //TODO - this call needs sorting
        return blockState.getBlock().getDrops(
                blockState,
                (ServerWorld) world,
                posBlock,
                TileUtil.getTile(world, posBlock)
        );

    }

    public static boolean tryPlantCocoaPod(IWorld world, BlockPos pos) {
        Direction facing = getValidPodFacing(world, pos);
        if (facing == null) {
            return false;
        }

        BlockState state = Blocks.COCOA.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, facing);
        world.setBlockState(pos, state, 18);
        return true;
    }

    @Nullable
    public static Direction getValidPodFacing(IWorld world, BlockPos pos) {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            if (isValidPodLocation(world, pos, facing)) {
                return facing;
            }
        }
        return null;
    }

    public static boolean isValidPodLocation(IWorldReader world, BlockPos pos, Direction direction) {
        pos = pos.offset(direction);
        if (!world.isBlockLoaded(pos)) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block.isIn(BlockTags.JUNGLE_LOGS);
    }

    public static boolean isBreakableBlock(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return isBreakableBlock(blockState, world, pos);
    }

    public static boolean isBreakableBlock(BlockState blockState, World world, BlockPos pos) {
        return blockState.getBlockHardness(world, pos) >= 0.0F;
    }

    public static boolean isReplaceableBlock(BlockState blockState, World world, BlockPos pos) {
        Block block = blockState.getBlock();
        return world.getBlockState(pos).getMaterial().isReplaceable() && !(block instanceof FlowingFluidBlock);
    }

    @Nullable
    public static RayTraceResult collisionRayTrace(
            BlockPos pos,
            Vector3d startVec,
            Vector3d endVec,
            AxisAlignedBB bounds
    ) {
        return collisionRayTrace(
                pos,
                startVec,
                endVec,
                bounds.minX,
                bounds.minY,
                bounds.minZ,
                bounds.maxX,
                bounds.maxY,
                bounds.maxZ
        );
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    //TODO - looks pretty copy pasted. Find new version as well? Is this still needed ?
    @Nullable
    public static RayTraceResult collisionRayTrace(
            BlockPos pos,
            Vector3d startVec,
            Vector3d endVec,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        startVec = startVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
        endVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
        Vector3d vec32 = startVec;//.getIntermediateWithXValue(endVec, minX);
        Vector3d vec33 = startVec;//.getIntermediateWithXValue(endVec, maxX);
        Vector3d vec34 = startVec;//.getIntermediateWithYValue(endVec, minY);
        Vector3d vec35 = startVec;//.getIntermediateWithYValue(endVec, maxY);
        Vector3d vec36 = startVec;//.getIntermediateWithZValue(endVec, minZ);
        Vector3d vec37 = startVec;//.getIntermediateWithZValue(endVec, maxZ);

        if (!isVecInsideYZBounds(vec32, minY, minZ, maxY, maxZ)) {
            vec32 = null;
        }

        if (!isVecInsideYZBounds(vec33, minY, minZ, maxY, maxZ)) {
            vec33 = null;
        }

        if (!isVecInsideXZBounds(vec34, minX, minZ, maxX, maxZ)) {
            vec34 = null;
        }

        if (!isVecInsideXZBounds(vec35, minX, minZ, maxX, maxZ)) {
            vec35 = null;
        }

        if (!isVecInsideXYBounds(vec36, minX, minY, maxX, maxY)) {
            vec36 = null;
        }

        if (!isVecInsideXYBounds(vec37, minX, minY, maxX, maxY)) {
            vec37 = null;
        }

        Vector3d minHit = null;

        if (vec32 != null) {
            minHit = vec32;
        }

        if (vec33 != null && (minHit == null || startVec.squareDistanceTo(vec33) < startVec.squareDistanceTo(minHit))) {
            minHit = vec33;
        }

        if (vec34 != null && (minHit == null || startVec.squareDistanceTo(vec34) < startVec.squareDistanceTo(minHit))) {
            minHit = vec34;
        }

        if (vec35 != null && (minHit == null || startVec.squareDistanceTo(vec35) < startVec.squareDistanceTo(minHit))) {
            minHit = vec35;
        }

        if (vec36 != null && (minHit == null || startVec.squareDistanceTo(vec36) < startVec.squareDistanceTo(minHit))) {
            minHit = vec36;
        }

        if (vec37 != null && (minHit == null || startVec.squareDistanceTo(vec37) < startVec.squareDistanceTo(minHit))) {
            minHit = vec37;
        }

        if (minHit == null) {
            return null;
        } else {
            byte sideHit = -1;

            if (minHit == vec32) {
                sideHit = 4;
            }

            if (minHit == vec33) {
                sideHit = 5;
            }

            if (minHit == vec34) {
                sideHit = 0;
            }

            if (minHit == vec35) {
                sideHit = 1;
            }

            if (minHit == vec36) {
                sideHit = 2;
            }

            if (minHit == vec37) {
                sideHit = 3;
            }

            return new BlockRayTraceResult(
                    minHit.add(pos.getX(), pos.getY(), pos.getZ()),
                    Direction.values()[sideHit],
                    pos,
                    true
            );
        }
    }

    /**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    private static boolean isVecInsideYZBounds(
            @Nullable Vector3d vec,
            double minY,
            double minZ,
            double maxY,
            double maxZ
    ) {
        return vec != null && vec.y >= minY && vec.y <= maxY && vec.z >= minZ && vec.z <= maxZ;
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    private static boolean isVecInsideXZBounds(
            @Nullable Vector3d vec,
            double minX,
            double minZ,
            double maxX,
            double maxZ
    ) {
        return vec != null && vec.x >= minX && vec.x <= maxX && vec.z >= minZ && vec.z <= maxZ;
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    private static boolean isVecInsideXYBounds(
            @Nullable Vector3d vec,
            double minX,
            double minY,
            double maxX,
            double maxY
    ) {
        return vec != null && vec.x >= minX && vec.x <= maxX && vec.y >= minY && vec.y <= maxY;
    }

    /* CHUNKS */

    public static boolean canReplace(BlockState blockState, IWorld world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable() && !blockState.getMaterial().isLiquid();
    }

    public static boolean canPlaceTree(BlockState blockState, IWorld world, BlockPos pos) {
        BlockPos downPos = pos.down();
        Block block = world.getBlockState(downPos).getBlock();
        return !(
                world.getBlockState(pos).getMaterial().isReplaceable() &&
                blockState.getMaterial().isLiquid()) &&
               !block.isIn(BlockTags.LOGS) &&
               !block.isIn(BlockTags.LEAVES);
    }

    public static BlockPos getNextReplaceableUpPos(World world, BlockPos pos) {
        BlockPos topPos = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos);
        final BlockPos.Mutable newPos = new BlockPos.Mutable();
        BlockState blockState = world.getBlockState(newPos.setPos(pos));

        while (!BlockUtil.canReplace(blockState, world, newPos)) {
            newPos.move(Direction.UP);
            if (newPos.getY() > topPos.getY()) {
                return null;
            }
            blockState = world.getBlockState(newPos);
        }

        return newPos.down();
    }

    @Nullable
    public static BlockPos getNextSolidDownPos(World world, BlockPos pos) {
        final BlockPos.Mutable newPos = new BlockPos.Mutable();

        BlockState blockState = world.getBlockState(newPos.setPos(pos));
        while (canReplace(blockState, world, newPos)) {
            newPos.move(Direction.DOWN);
            if (newPos.getY() <= 0) {
                return null;
            }
            blockState = world.getBlockState(newPos);
        }
        return newPos.up();
    }


    public static boolean setBlockWithPlaceSound(World world, BlockPos pos, BlockState blockState) {
        if (world.setBlockState(pos, blockState)) {
            PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
            NetworkUtil.sendNetworkPacket(packet, pos, world);
            return true;
        }
        return false;
    }

    public static boolean setBlockWithBreakSound(
            World world,
            BlockPos pos,
            BlockState blockState,
            BlockState oldState
    ) {
        if (world.setBlockState(pos, blockState)) {
            PacketFXSignal packet = new PacketFXSignal(
                    PacketFXSignal.VisualFXType.BLOCK_BREAK,
                    PacketFXSignal.SoundFXType.BLOCK_BREAK,
                    pos,
                    oldState
            );
            NetworkUtil.sendNetworkPacket(packet, pos, world);
            return true;
        }
        return false;
    }

    public static boolean setBlockToAirWithSound(World world, BlockPos pos, BlockState oldState) {
        if (world.removeBlock(pos, false)) {
            PacketFXSignal packet = new PacketFXSignal(
                    PacketFXSignal.VisualFXType.BLOCK_BREAK,
                    PacketFXSignal.SoundFXType.BLOCK_BREAK,
                    pos,
                    oldState
            );
            NetworkUtil.sendNetworkPacket(packet, pos, world);
            return true;
        }
        return false;
    }
}
