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

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockWall;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;

import net.minecraftforge.common.IPlantable;

public class AIButterflyRest extends AIButterflyBase {

	public AIButterflyRest(EntityButterfly entity) {
		super(entity);
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		if (entity.getExhaustion() < EntityButterfly.EXHAUSTION_REST
				&& entity.canFly()) {
			return false;
		}

		int x = (int) entity.posX;
		int y = ((int) Math.floor(entity.posY));
		int z = (int) entity.posZ;

		if (!canLand(x, y, z)) {
			return false;
		}

		y--;
		if (entity.worldObj.isAirBlock(x, y, z)) {
			return false;
		}
		if (entity.worldObj.getBlock(x, y, z).getMaterial().isLiquid()) {
			return false;
		}
		if (!entity.getButterfly().isAcceptedEnvironment(entity.worldObj, x, y, z)) {
			return false;
		}

		entity.setDestination(null);
		entity.setState(EnumButterflyState.RESTING);
		return true;
	}

	@Override
	public boolean continueExecuting() {
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
	public void updateTask() {
		entity.changeExhaustion(-1);
	}

	private boolean canLand(int x, int y, int z) {
		Block block = entity.worldObj.getBlock(x, y, z);
		// getBlocksMovement is a bad name, allowsMovement would be a better name.
		if (!block.getBlocksMovement(entity.worldObj, x, y, z)) {
			return false;
		}
		if (isPlant(block)) {
			return true;
		}
		block = entity.worldObj.getBlock(x, y - 1, z);
		return isRest(block) || block.isLeaves(entity.worldObj, x, y - 1, z);
	}

	private static boolean isRest(Block block) {
		if (block instanceof BlockFence) {
			return true;
		}
		return block instanceof BlockWall;
	}

	private static boolean isPlant(Block block) {
		if (block instanceof BlockFlower) {
			return true;
		} else if (block instanceof IPlantable) {
			return true;
		} else if (block instanceof IGrowable) {
			return true;
		} else if (block.getMaterial() == Material.plants) {
			return true;
		}
		return false;
	}
}
