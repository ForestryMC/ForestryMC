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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import forestry.core.config.Constants;
import forestry.core.tiles.TileEngine;
import forestry.core.utils.vect.Vect;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;

public abstract class BlockUtil {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");

	public static ArrayList<ItemStack> getBlockDrops(World world, Vect posBlock) {
		Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);
		int meta = world.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);

		return block.getDrops(world, posBlock.x, posBlock.y, posBlock.z, meta, 0);

	}

	public static boolean isEnergyReceiverOrEngine(ForgeDirection side, TileEntity tile) {
		if (!(tile instanceof IEnergyReceiver) && !(tile instanceof TileEngine)) {
			return false;
		}

		IEnergyConnection receptor = (IEnergyConnection) tile;
		return receptor.canConnectEnergy(side);
	}

	public static boolean tryPlantPot(World world, int x, int y, int z, Block block) {

		int direction = getDirectionalMetadata(world, x, y, z);
		if (direction < 0) {
			return false;
		}

		world.setBlock(x, y, z, block, direction, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		return true;
	}

	public static int getDirectionalMetadata(World world, int x, int y, int z) {
		for (int i = 0; i < 4; i++) {
			if (!isValidPot(world, x, y, z, i)) {
				continue;
			}
			return i;
		}
		return -1;
	}

	public static boolean isValidPot(World world, int x, int y, int z, int notchDirection) {
		x += Direction.offsetX[notchDirection];
		z += Direction.offsetZ[notchDirection];
		Block block = world.getBlock(x, y, z);
		if (block == Blocks.log) {
			return BlockLog.func_150165_c(world.getBlockMetadata(x, y, z)) == 3;
		} else {
			return block.isWood(world, x, y, z);
		}
	}

	public static int getMaturityPod(int metadata) {
		return BlockCocoa.func_149987_c(metadata);
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

	public static boolean isReplaceableBlock(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		return isReplaceableBlock(block);
	}

	public static boolean isReplaceableBlock(Block block) {
		return block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer
				|| block.getMaterial().isReplaceable();
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
	 */
	public static MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startVec, Vec3 endVec, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		startVec = startVec.addVector((double) (-x), (double) (-y), (double) (-z));
		endVec = endVec.addVector((double) (-x), (double) (-y), (double) (-z));
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

			return new MovingObjectPosition(x, y, z, sideHit, minHit.addVector((double) x, (double) y, (double) z));
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
}
