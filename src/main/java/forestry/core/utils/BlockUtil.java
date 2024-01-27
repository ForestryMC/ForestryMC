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
package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;

import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;


public abstract class BlockUtil {


	public static boolean alwaysTrue(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	public static boolean alwaysFalse(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}

	public static List<ItemStack> getBlockDrops(LevelAccessor world, BlockPos posBlock) {
		BlockState blockState = world.getBlockState(posBlock);

		//TODO - this call needs sorting
		return blockState.getBlock().getDrops(blockState, (ServerLevel) world, posBlock, TileUtil.getTile(world, posBlock));

	}

	public static boolean tryPlantCocoaPod(LevelAccessor world, BlockPos pos) {
		Direction facing = getValidPodFacing(world, pos, BlockTags.JUNGLE_LOGS);
		if (facing == null) {
			return false;
		}

		BlockState state = Blocks.COCOA.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);
		world.setBlock(pos, state, 18);
		return true;
	}

	@Nullable
	public static Direction getValidPodFacing(LevelAccessor world, BlockPos pos, TagKey<Block> logTag) {
		for (Direction facing : Direction.Plane.HORIZONTAL) {
			if (isValidPodLocation(world, pos, facing, logTag)) {
				return facing;
			}
		}
		return null;
	}

	public static boolean isValidPodLocation(LevelReader world, BlockPos pos, Direction direction, TagKey<Block> logTag) {
		pos = pos.relative(direction);
		if (!world.hasChunkAt(pos)) {
			return false;
		}
		BlockState state = world.getBlockState(pos);
		return state.is(logTag);
	}

	public static boolean isBreakableBlock(Level world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return isBreakableBlock(blockState, world, pos);
	}

	public static boolean isBreakableBlock(BlockState blockState, Level world, BlockPos pos) {
		return blockState.getDestroySpeed(world, pos) >= 0.0F;
	}

	public static boolean isReplaceableBlock(BlockState blockState, Level world, BlockPos pos) {
		Block block = blockState.getBlock();
		return world.getBlockState(pos).getMaterial().isReplaceable() && true;//!(block instanceof BlockStaticLiquid);
	}

	@Nullable
	public static HitResult collisionRayTrace(BlockPos pos, Vec3 startVec, Vec3 endVec, AABB bounds) {
		return collisionRayTrace(pos, startVec, endVec, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
	 */
	//TODO - looks pretty copy pasted. Find new version as well? Is this still needed ?
	@Nullable
	public static HitResult collisionRayTrace(BlockPos pos, Vec3 startVec, Vec3 endVec, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		startVec = startVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
		endVec = endVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
		Vec3 vec32 = startVec;//.getIntermediateWithXValue(endVec, minX);
		Vec3 vec33 = startVec;//.getIntermediateWithXValue(endVec, maxX);
		Vec3 vec34 = startVec;//.getIntermediateWithYValue(endVec, minY);
		Vec3 vec35 = startVec;//.getIntermediateWithYValue(endVec, maxY);
		Vec3 vec36 = startVec;//.getIntermediateWithZValue(endVec, minZ);
		Vec3 vec37 = startVec;//.getIntermediateWithZValue(endVec, maxZ);

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

		Vec3 minHit = null;

		if (vec32 != null) {
			minHit = vec32;
		}

		if (vec33 != null && (minHit == null || startVec.distanceToSqr(vec33) < startVec.distanceToSqr(minHit))) {
			minHit = vec33;
		}

		if (vec34 != null && (minHit == null || startVec.distanceToSqr(vec34) < startVec.distanceToSqr(minHit))) {
			minHit = vec34;
		}

		if (vec35 != null && (minHit == null || startVec.distanceToSqr(vec35) < startVec.distanceToSqr(minHit))) {
			minHit = vec35;
		}

		if (vec36 != null && (minHit == null || startVec.distanceToSqr(vec36) < startVec.distanceToSqr(minHit))) {
			minHit = vec36;
		}

		if (vec37 != null && (minHit == null || startVec.distanceToSqr(vec37) < startVec.distanceToSqr(minHit))) {
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

			return new BlockHitResult(minHit.add(pos.getX(), pos.getY(), pos.getZ()), Direction.values()[sideHit], pos, true);
		}
	}

	/**
	 * Checks if a vector is within the Y and Z bounds of the block.
	 */
	private static boolean isVecInsideYZBounds(@Nullable Vec3 vec, double minY, double minZ, double maxY, double maxZ) {
		return vec != null && vec.y >= minY && vec.y <= maxY && vec.z >= minZ && vec.z <= maxZ;
	}

	/**
	 * Checks if a vector is within the X and Z bounds of the block.
	 */
	private static boolean isVecInsideXZBounds(@Nullable Vec3 vec, double minX, double minZ, double maxX, double maxZ) {
		return vec != null && vec.x >= minX && vec.x <= maxX && vec.z >= minZ && vec.z <= maxZ;
	}

	/**
	 * Checks if a vector is within the X and Y bounds of the block.
	 */
	private static boolean isVecInsideXYBounds(@Nullable Vec3 vec, double minX, double minY, double maxX, double maxY) {
		return vec != null && vec.x >= minX && vec.x <= maxX && vec.y >= minY && vec.y <= maxY;
	}

	/* CHUNKS */

	public static boolean canReplace(BlockState blockState, LevelAccessor world, BlockPos pos) {
		return world.getBlockState(pos).getMaterial().isReplaceable() && !blockState.getMaterial().isLiquid();
	}

	public static boolean canPlaceTree(BlockState blockState, LevelAccessor world, BlockPos pos) {
		BlockPos downPos = pos.below();
		Block block = world.getBlockState(downPos).getBlock();
		return !(world.getBlockState(pos).getMaterial().isReplaceable() &&
				blockState.getMaterial().isLiquid()) && true;
		//			!block.isLeaves(blockState, world, downPos) &&
		//			!block.isWood(world, downPos);
	}

	public static BlockPos getNextReplaceableUpPos(Level world, BlockPos pos) {
		BlockPos topPos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
		final BlockPos.MutableBlockPos newPos = new BlockPos.MutableBlockPos();
		BlockState blockState = world.getBlockState(newPos.set(pos));

		while (!BlockUtil.canReplace(blockState, world, newPos)) {
			newPos.move(Direction.UP);
			if (newPos.getY() > topPos.getY()) {
				return null;
			}
			blockState = world.getBlockState(newPos);
		}

		return newPos.below();
	}

	@Nullable
	public static BlockPos getNextSolidDownPos(Level world, BlockPos pos) {
		final BlockPos.MutableBlockPos newPos = new BlockPos.MutableBlockPos();

		BlockState blockState = world.getBlockState(newPos.set(pos));
		while (canReplace(blockState, world, newPos)) {
			newPos.move(Direction.DOWN);
			if (newPos.getY() <= 0) {
				return null;
			}
			blockState = world.getBlockState(newPos);
		}
		return newPos.above();
	}


	public static boolean setBlockWithPlaceSound(Level world, BlockPos pos, BlockState blockState) {
		if (world.setBlockAndUpdate(pos, blockState)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	public static boolean setBlockWithBreakSound(Level world, BlockPos pos, BlockState blockState, BlockState oldState) {
		if (world.setBlockAndUpdate(pos, blockState)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, oldState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	public static boolean setBlockToAirWithSound(Level world, BlockPos pos, BlockState oldState) {
		if (world.removeBlock(pos, false)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, oldState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}
}
