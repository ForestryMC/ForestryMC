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
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.tiles.TileEngine;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;

public abstract class BlockUtil {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");

	public static List<ItemStack> getBlockDrops(World world, Vect posBlock) {
		IBlockState blockState = VectUtil.getBlockState(world, posBlock);

		return blockState.getBlock().getDrops(world, posBlock, blockState, 0);

	}
	
	/**
	 * @return The block state from the pos
	 */
	public static IBlockState getBlockState(IBlockAccess world, BlockPos posBlock) {
		return world.getBlockState(posBlock);
	}
	
	/**
	 * @return The block from the pos
	 */
	public static Block getBlock(IBlockAccess world, BlockPos posBlock) {
		return getBlockState(world, posBlock).getBlock();
	}
	
	/**
	 * @return The block metadata from the pos
	 */
	public static int getBlockMetadata(IBlockAccess world, BlockPos posBlock) {
		return getBlock(world, posBlock).getMetaFromState(getBlockState(world, posBlock));
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

		IBlockState state = Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.FACING, facing);
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
		if (block == Blocks.log) {
			return state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
		} else {
			return block.isWood(world, pos);
		}
	}

	public static boolean isWoodSlabBlock(Block block, IBlockAccess world, BlockPos pos) {
		if (block == null || block.isAir(world, pos)) {
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

	public static boolean isReplaceableBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		return isReplaceableBlock(block);
	}

	public static boolean isReplaceableBlock(Block block) {
		return block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer
				|| block.getMaterial().isReplaceable();
	}

	public static MovingObjectPosition collisionRayTrace(@Nonnull BlockPos pos, @Nonnull Vec3 startVec, @Nonnull Vec3 endVec, @Nonnull AxisAlignedBB bounds) {
		return collisionRayTrace(pos, startVec, endVec, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
	 */
	public static MovingObjectPosition collisionRayTrace(@Nonnull BlockPos pos, @Nonnull Vec3 startVec, @Nonnull Vec3 endVec, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		startVec = startVec.addVector((-pos.getX()), (-pos.getY()), (-pos.getZ()));
		endVec = endVec.addVector((-pos.getX()), (-pos.getY()), (-pos.getZ()));
		Vec3 vec32 = startVec.getIntermediateWithXValue(endVec, minX);
		Vec3 vec33 = startVec.getIntermediateWithXValue(endVec, maxX);
		Vec3 vec34 = startVec.getIntermediateWithYValue(endVec, minY);
		Vec3 vec35 = startVec.getIntermediateWithYValue(endVec, maxY);
		Vec3 vec36 = startVec.getIntermediateWithZValue(endVec, minZ);
		Vec3 vec37 = startVec.getIntermediateWithZValue(endVec, maxZ);

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

			return new MovingObjectPosition(minHit.addVector(pos.getX(), pos.getY(), pos.getZ()), EnumFacing.values()[sideHit], pos);
		}
	}

	/**
	 * Checks if a vector is within the Y and Z bounds of the block.
	 */
	private static boolean isVecInsideYZBounds(Vec3 vec, double minY, double minZ, double maxY, double maxZ) {
		return vec != null && (vec.yCoord >= minY && vec.yCoord <= maxY && vec.zCoord >= minZ && vec.zCoord <= maxZ);
	}

	/**
	 * Checks if a vector is within the X and Z bounds of the block.
	 */
	private static boolean isVecInsideXZBounds(Vec3 vec, double minX, double minZ, double maxX, double maxZ) {
		return vec != null && (vec.xCoord >= minX && vec.xCoord <= maxX && vec.zCoord >= minZ && vec.zCoord <= maxZ);
	}

	/**
	 * Checks if a vector is within the X and Y bounds of the block.
	 */
	private static boolean isVecInsideXYBounds(Vec3 vec, double minX, double minY, double maxX, double maxY) {
		return vec != null && (vec.xCoord >= minX && vec.xCoord <= maxX && vec.yCoord >= minY && vec.yCoord <= maxY);
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
					if (!world.getChunkProvider().chunkExists(k1, l1)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
