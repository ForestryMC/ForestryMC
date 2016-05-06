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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.tiles.TileEngine;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;

public abstract class BlockUtil {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");

	public static List<ItemStack> getBlockDrops(World world, BlockPos posBlock) {
		IBlockState blockState = world.getBlockState(posBlock);

		return blockState.getBlock().getDrops(world, posBlock, blockState, 0);

	}
	
	public static boolean isEnergyReceiverOrEngine(EnumFacing side, TileEntity tile) {
		if (!(tile instanceof IEnergyReceiver) && !(tile instanceof TileEngine)) {
			return false;
		}

		IEnergyConnection receptor = (IEnergyConnection) tile;
		return receptor.canConnectEnergy(side);
	}

	public static boolean tryPlantCocoaPod(World world, BlockPos pos) {

		EnumFacing facing = getValidPodFacing(world, pos);
		if (facing == null) {
			return false;
		}

		IBlockState state = Blocks.COCOA.getDefaultState().withProperty(BlockDirectional.FACING, facing);
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
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.LOG) {
			return state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
		} else {
			return block.isWood(world, pos);
		}
	}

	public static boolean isWoodSlabBlock(IBlockState blockState, Block block, IBlockAccess world, BlockPos pos) {
		if (blockState == null || block == null || block.isAir(blockState, world, pos)) {
			return false;
		}
		if(Item.getItemFromBlock(block) == null){
			return false;
		}
		int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block));
		for (int oreId : oreIds) {
			if (oreId == slabWoodId) {
				return true;
			}
		}

		return false;
	}

	public static boolean isReplaceableBlock(IBlockState blockState, World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		return isReplaceableBlock(blockState, block);
	}

	public static boolean isReplaceableBlock(IBlockState blockState, Block block) {
		return block.getMaterial(blockState).isReplaceable();
	}

	public static RayTraceResult collisionRayTrace(@Nonnull BlockPos pos, @Nonnull Vec3d startVec, @Nonnull Vec3d endVec, @Nonnull AxisAlignedBB bounds) {
		return collisionRayTrace(pos, startVec, endVec, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
	 */
	public static RayTraceResult collisionRayTrace(@Nonnull BlockPos pos, @Nonnull Vec3d startVec, @Nonnull Vec3d endVec, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		startVec = startVec.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
		endVec = endVec.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
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

		if (vec32 != null && (minHit == null || startVec.squareDistanceTo(vec32) < startVec.squareDistanceTo(minHit))) {
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

			return new RayTraceResult(minHit.addVector(pos.getX(), pos.getY(), pos.getZ()), EnumFacing.values()[sideHit], pos);
		}
	}

	/**
	 * Checks if a vector is within the Y and Z bounds of the block.
	 */
	private static boolean isVecInsideYZBounds(Vec3d vec, double minY, double minZ, double maxY, double maxZ) {
		return vec != null && vec.yCoord >= minY && vec.yCoord <= maxY && vec.zCoord >= minZ && vec.zCoord <= maxZ;
	}

	/**
	 * Checks if a vector is within the X and Z bounds of the block.
	 */
	private static boolean isVecInsideXZBounds(Vec3d vec, double minX, double minZ, double maxX, double maxZ) {
		return vec != null && vec.xCoord >= minX && vec.xCoord <= maxX && vec.zCoord >= minZ && vec.zCoord <= maxZ;
	}

	/**
	 * Checks if a vector is within the X and Y bounds of the block.
	 */
	private static boolean isVecInsideXYBounds(Vec3d vec, double minX, double minY, double maxX, double maxY) {
		return vec != null && vec.xCoord >= minX && vec.xCoord <= maxX && vec.yCoord >= minY && vec.yCoord <= maxY;
	}
	
	/* CHUNKS */

	/**
	 * Checks if chunk exits.
	 */
	public static boolean checkChunksExist(World world, BlockPos minPos, BlockPos maxPos) {
		return checkChunksExist(world, minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
	}

	/**
	 * Checks if chunk exits.
	 */
	public static boolean checkChunksExist(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		if (maxY >= 0 && minY < 256) {
			minX >>= 4;
			minZ >>= 4;
			maxX >>= 4;
			maxZ >>= 4;

			for (int k1 = minX; k1 <= maxX; ++k1) {
				for (int l1 = minZ; l1 <= maxZ; ++l1) {
					if (world.getChunkProvider().getLoadedChunk(k1, l1) == null) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
	
	public static boolean canReplace(IBlockState blockState, World world, BlockPos pos) {
		Block block = blockState.getBlock();
		Material material = block.getMaterial(blockState);
		return material.isReplaceable() && block.isReplaceable(world, pos) && !material.isLiquid() || block.isAir(blockState, world, pos) || material == Material.PLANTS;
	}
	
	public static boolean canPlaceTree(IBlockState blockState, World world, BlockPos pos){
		BlockPos downPos = pos.down();
		Block block = world.getBlockState(downPos).getBlock();
		if(block.isReplaceable(world, downPos) && block.getMaterial(blockState).isLiquid() || block.isLeaves(blockState, world, downPos) || block.isWood(world, downPos)){
			return false;
		}
		return true;
	}
	
	public static BlockPos getNextReplaceableUpPos(World world, BlockPos pos){
		IBlockState blockState;

		do {
			pos = pos.up();
			blockState = world.getBlockState(pos);
		} while (!BlockUtil.canReplace(blockState, world, pos));

		return pos;
	}
	
	public static BlockPos getNextSolidDownPos(World world, BlockPos pos){
		IBlockState blockState;

		do {
			pos = pos.down();
			blockState = world.getBlockState(pos);
		} while (BlockUtil.canReplace(blockState, world, pos));

		return pos;
	}

	/** Copied from {@link Block#shouldSideBeRendered} */
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
}
