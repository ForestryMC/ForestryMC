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
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

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
		int y = (int) Math.floor(entity.posY);
		int z = (int) entity.posZ;
		BlockPos pos = new BlockPos(x, y, z);

		if (!canLand(pos)) {
			return false;
		}

		pos = pos.add(x, -1, z);
		if (entity.world.isAirBlock(pos)) {
			return false;
		}
		IBlockState blockState = entity.world.getBlockState(pos);
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
	public void updateTask() {
		entity.changeExhaustion(-1);
	}

	private boolean canLand(BlockPos pos) {
		if (!entity.world.isBlockLoaded(pos)) {
			return false;
		}
		IBlockState blockState = entity.world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!block.isPassable(entity.world, pos)) {
			return false;
		}
		if (isPlant(blockState)) {
			return true;
		}

		IBlockState blockStateBelow = entity.world.getBlockState(pos.down());
		Block blockBelow = blockStateBelow.getBlock();
		return isRest(blockBelow) || blockBelow.isLeaves(blockStateBelow, entity.world, pos.down());
	}

	private static boolean isRest(Block block) {
		if (block instanceof BlockFence) {
			return true;
		}
		return block instanceof BlockWall;
	}

	private static boolean isPlant(IBlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof BlockFlower) {
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
