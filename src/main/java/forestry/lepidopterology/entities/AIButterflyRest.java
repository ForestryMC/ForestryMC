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
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.IPlantable;

import forestry.lepidopterology.entities.EntityButterfly.EnumButterflyState;

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
		BlockPos pos = new BlockPos(x, y, z);
		if (entity.worldObj.isAirBlock(pos)) {
			return false;
		}
		if (entity.worldObj.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
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
		Block block = entity.worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();
		if (isPlant(block)) {
			return true;
		}
		block = entity.worldObj.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
		return isRest(block) || block.isLeaves(entity.worldObj, new BlockPos(x, y - 1, z));
	}

	private boolean isRest(Block block) {
		if (block instanceof BlockFence) {
			return true;
		}
		return block instanceof BlockWall;
	}

	private boolean isPlant(Block block) {
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
