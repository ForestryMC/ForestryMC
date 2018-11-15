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

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.network.packets.PacketFXSignal;

public abstract class BlockUtil {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");

	public static List<ItemStack> getBlockDrops(World world, BlockPos posBlock) {
		IBlockState blockState = world.getBlockState(posBlock);

		return blockState.getBlock().getDrops(world, posBlock, blockState, 0);

	}

	public static boolean tryPlantCocoaPod(World world, BlockPos pos) {

		EnumFacing facing = getValidPodFacing(world, pos);
		if (facing == null) {
			return false;
		}

		IBlockState state = Blocks.COCOA.getDefaultState().withProperty(BlockHorizontal.FACING, facing);
		world.setBlockState(pos, state);
		return true;
	}

	@Nullable
	public static EnumFacing getValidPodFacing(World world, BlockPos pos) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (isValidPodLocation(world, pos, facing)) {
				return facing;
			}
		}
		return null;
	}

	public static boolean isValidPodLocation(World world, BlockPos pos, EnumFacing direction) {
		pos = pos.offset(direction);
		if (!world.isBlockLoaded(pos)) {
			return false;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.LOG) {
			return state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE;
		} else {
			return block.isWood(world, pos);
		}
	}

	public static boolean isWoodSlabBlock(IBlockState blockState, Block block, IBlockAccess world, BlockPos pos) {
		if (block.isAir(blockState, world, pos)) {
			return false;
		}

		ItemStack stack = new ItemStack(block);
		if (stack.isEmpty()) {
			return false;
		}

		int[] oreIds = OreDictionary.getOreIDs(stack);
		for (int oreId : oreIds) {
			if (oreId == slabWoodId) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBreakableBlock(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return isBreakableBlock(blockState, world, pos);
	}

	public static boolean isBreakableBlock(IBlockState blockState, World world, BlockPos pos) {
		return blockState.getBlockHardness(world, pos) >= 0.0F;
	}

	public static boolean isReplaceableBlock(IBlockState blockState, World world, BlockPos pos) {
		Block block = blockState.getBlock();
		return block.isReplaceable(world, pos) && !(block instanceof BlockStaticLiquid);
	}

	@Nullable
	public static RayTraceResult collisionRayTrace(BlockPos pos, Vec3d startVec, Vec3d endVec, AxisAlignedBB bounds) {
		return collisionRayTrace(pos, startVec, endVec, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
	 */
	@Nullable
	public static RayTraceResult collisionRayTrace(BlockPos pos, Vec3d startVec, Vec3d endVec, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		startVec = startVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
		endVec = endVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
		Vec3d vec32 = startVec.getIntermediateWithXValue(endVec, minX);
		Vec3d vec33 = startVec.getIntermediateWithXValue(endVec, maxX);
		Vec3d vec34 = startVec.getIntermediateWithYValue(endVec, minY);
		Vec3d vec35 = startVec.getIntermediateWithYValue(endVec, maxY);
		Vec3d vec36 = startVec.getIntermediateWithZValue(endVec, minZ);
		Vec3d vec37 = startVec.getIntermediateWithZValue(endVec, maxZ);

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

		Vec3d minHit = null;

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

			return new RayTraceResult(minHit.add(pos.getX(), pos.getY(), pos.getZ()), EnumFacing.values()[sideHit], pos);
		}
	}

	/**
	 * Checks if a vector is within the Y and Z bounds of the block.
	 */
	private static boolean isVecInsideYZBounds(@Nullable Vec3d vec, double minY, double minZ, double maxY, double maxZ) {
		return vec != null && vec.y >= minY && vec.y <= maxY && vec.z >= minZ && vec.z <= maxZ;
	}

	/**
	 * Checks if a vector is within the X and Z bounds of the block.
	 */
	private static boolean isVecInsideXZBounds(@Nullable Vec3d vec, double minX, double minZ, double maxX, double maxZ) {
		return vec != null && vec.x >= minX && vec.x <= maxX && vec.z >= minZ && vec.z <= maxZ;
	}

	/**
	 * Checks if a vector is within the X and Y bounds of the block.
	 */
	private static boolean isVecInsideXYBounds(@Nullable Vec3d vec, double minX, double minY, double maxX, double maxY) {
		return vec != null && vec.x >= minX && vec.x <= maxX && vec.y >= minY && vec.y <= maxY;
	}

	/* CHUNKS */

	public static boolean canReplace(IBlockState blockState, World world, BlockPos pos) {
		Block block = blockState.getBlock();
		return block.isReplaceable(world, pos) && !blockState.getMaterial().isLiquid();
	}

	public static boolean canPlaceTree(IBlockState blockState, World world, BlockPos pos) {
		BlockPos downPos = pos.down();
		Block block = world.getBlockState(downPos).getBlock();
		return !(block.isReplaceable(world, downPos) &&
			blockState.getMaterial().isLiquid()) &&
			!block.isLeaves(blockState, world, downPos) &&
			!block.isWood(world, downPos);
	}

	public static BlockPos getNextReplaceableUpPos(World world, BlockPos pos) {
		BlockPos topPos = world.getHeight(pos);
		final BlockPos.MutableBlockPos newPos = new BlockPos.MutableBlockPos(pos);
		IBlockState blockState = world.getBlockState(newPos);

		while (!BlockUtil.canReplace(blockState, world, newPos)) {
			newPos.move(EnumFacing.UP);
			if (newPos.getY() > topPos.getY()) {
				return null;
			}
			blockState = world.getBlockState(newPos);
		}

		return newPos.down();
	}

	public static BlockPos getNextSolidDownPos(World world, BlockPos pos) {
		final BlockPos.MutableBlockPos newPos = new BlockPos.MutableBlockPos(pos);

		IBlockState blockState = world.getBlockState(newPos);
		while (canReplace(blockState, world, newPos)) {
			newPos.move(EnumFacing.DOWN);
			if (newPos.getY() <= 0) {
				return null;
			}
			blockState = world.getBlockState(newPos);
		}
		return newPos.up();
	}

	/**
	 * Copied from {@link Block#shouldSideBeRendered}
	 */
	public static boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		AxisAlignedBB axisalignedbb = blockState.getBoundingBox(blockAccess, pos);

		switch (side) {
			case DOWN:
				if (axisalignedbb.minY > 0.0D) {
					return true;
				}
				break;
			case UP:
				if (axisalignedbb.maxY < 1.0D) {
					return true;
				}
				break;
			case NORTH:
				if (axisalignedbb.minZ > 0.0D) {
					return true;
				}
				break;
			case SOUTH:
				if (axisalignedbb.maxZ < 1.0D) {
					return true;
				}
				break;
			case WEST:
				if (axisalignedbb.minX > 0.0D) {
					return true;
				}
				break;
			case EAST:
				if (axisalignedbb.maxX < 1.0D) {
					return true;
				}
		}

		return !blockAccess.getBlockState(pos.offset(side)).doesSideBlockRendering(blockAccess, pos.offset(side), side.getOpposite());
	}

	public static boolean setBlockWithPlaceSound(World world, BlockPos pos, IBlockState blockState) {
		if (world.setBlockState(pos, blockState)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	public static boolean setBlockWithBreakSound(World world, BlockPos pos, IBlockState blockState, IBlockState oldState) {
		if (world.setBlockState(pos, blockState)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, oldState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	public static boolean setBlockToAirWithSound(World world, BlockPos pos, IBlockState oldState) {
		if (world.setBlockToAir(pos)) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, oldState);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	@Nullable
	public static <T extends Comparable<T>> IProperty<T> getProperty(Block block, String propertyName, Class<T> valueClass) {
		for (IProperty<?> property : block.getDefaultState().getPropertyKeys()) {
			if (property.getName().equals(propertyName)) {
				if (property.getValueClass().isAssignableFrom(valueClass)) {
					//noinspection unchecked
					return (IProperty<T>) property;
				}
			}
		}
		return null;
	}
}
