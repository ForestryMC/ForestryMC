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

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.config.Constants;
import forestry.core.tiles.TileEngine;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;

public abstract class BlockUtil {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");

	public static List<ItemStack> getBlockDrops(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		return state.getBlock().getDrops(world, pos, state, 0);

	}

	public static boolean isEnergyReceiverOrEngine(EnumFacing side, TileEntity tile) {
		if (!(tile instanceof IEnergyReceiver) && !(tile instanceof TileEngine)) {
			return false;
		}

		IEnergyConnection receptor = (IEnergyConnection) tile;
		return receptor.canConnectEnergy(side);
	}

	public static boolean tryPlantPot(World world, BlockPos pos, Block block) {

		int direction = getDirectionalMetadata(world, pos);
		if (direction < 0) {
			return false;
		}
		
		IBlockState state = block.getStateFromMeta(direction);

		world.setBlockState(pos, state, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		return true;
	}

	public static int getDirectionalMetadata(World world, BlockPos pos) {
		for (int i = 2; i < 4; i++) {
			if (!isValidPot(world, pos, EnumFacing.values()[i])) {
				continue;
			}
			return i;
		}
		return -1;
	}

	public static boolean isValidPot(World world, BlockPos pos, EnumFacing direction) {
		pos = pos.offset(direction);
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.log) {
			return state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
		} else {
			return state.getBlock().isWood(world, pos);
		}
	}

	public static int getMaturityPod(IBlockState state) {
		return state.getValue(BlockCocoa.AGE).intValue();
	}

	public static boolean isWoodSlabBlock(Block block) {
		int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block));
		for (int oreId : oreIds) {
			if (oreId == slabWoodId) {
				return true;
			}
		}

		return false;
	}

	public static boolean isReplaceableBlock(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		return isReplaceableBlock(state.getBlock());
	}

	public static boolean isReplaceableBlock(Block block) {
		return block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer || block.getMaterial().isReplaceable();
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
	 */
	public static MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 startVec, Vec3 endVec, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
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
	private static boolean isVecInsideYZBounds(Vec3 vec, float minY, float minZ, float maxY, float maxZ) {
		return vec != null && (vec.yCoord >= minY && vec.yCoord <= maxY && vec.zCoord >= minZ && vec.zCoord <= maxZ);
	}

	/**
	 * Checks if a vector is within the X and Z bounds of the block.
	 */
	private static boolean isVecInsideXZBounds(Vec3 vec, float minX, float minZ, float maxX, float maxZ) {
		return vec != null && (vec.xCoord >= minX && vec.xCoord <= maxX && vec.zCoord >= minZ && vec.zCoord <= maxZ);
	}

	/**
	 * Checks if a vector is within the X and Y bounds of the block.
	 */
	private static boolean isVecInsideXYBounds(Vec3 vec, float minX, float minY, float maxX, float maxY) {
		return vec != null && (vec.xCoord >= minX && vec.xCoord <= maxX && vec.yCoord >= minY && vec.yCoord <= maxY);
	}
	
	public static boolean blockExists(World world, BlockPos pos) {
		return pos.getY() >= 0 && pos.getY() < 256
				? world.getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4) : false;
	}
	
	public static boolean checkChunksExist(World world, BlockPos minPos, BlockPos maxPos) {
		return checkChunksExist(world, minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
	}

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
	
	/*
	 * BLOCK POS
	 */
	 
	public static BlockPos multiply(BlockPos pos, int factor) {
		return new BlockPos(pos.getX() * factor, pos.getY() * factor, pos.getZ() * factor);
	}

	public static BlockPos multiply(BlockPos pos, float factor) {
		return new BlockPos(Math.round(pos.getX() * factor), Math.round(pos.getY() * factor), Math.round(pos.getZ() * factor));
	}
	
	public static BlockPos getRandomPositionInArea(Random random, BlockPos area) {
		int x = random.nextInt(area.getX());
		int y = random.nextInt(area.getY());
		int z = random.nextInt(area.getZ());
		return new BlockPos(x, y, z);
	}

	public static BlockPos add(BlockPos... vects) {
		int x = 0;
		int y = 0;
		int z = 0;
		for (BlockPos vect : vects) {
			x += vect.getX();
			y += vect.getY();
			z += vect.getZ();
		}
		return new BlockPos(x, y, z);
	}
	
	public static boolean advancePositionInArea(BlockPos pos, BlockPos area) {
		// Increment z first until end reached
		if (pos.getZ() < area.getZ() - 1) {
			pos.add(0, 0, 1);
		} else {
			pos = new BlockPos(pos.getX(), pos.getY(), 0);

			if (pos.getX() < area.getX() - 1) {
				pos.add(1, 0, 0);
			} else {
				pos = new BlockPos(0, pos.getY(), 0);

				if (pos.getY() < area.getY() - 1) {
					pos.add(0, 1, 0);
				} else {
					return false;
				}
			}
		}

		return true;
	}
	
}
