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

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraftforge.common.IPlantable;

public class AIButterflyRest extends AIButterflyBase {

	public AIButterflyRest(EntityButterfly entity) {
		super(entity);
		setFlags(EnumSet.of(Flag.MOVE));
		//		setMutexBits(3);	TODO mutex
	}

	@Override
	public boolean canUse() {

		if (entity.getExhaustion() < EntityButterfly.EXHAUSTION_REST
				&& entity.canFly()) {
			return false;
		}

		Vector3d entityPos = entity.position();
		int x = (int) entityPos.x;
		int y = (int) Math.floor(entityPos.y);
		int z = (int) entityPos.z;
		BlockPos pos = new BlockPos(x, y, z);

		if (!canLand(pos)) {
			return false;
		}

		pos = pos.relative(Direction.DOWN);
		if (entity.level.isEmptyBlock(pos)) {
			return false;
		}
		BlockState blockState = entity.level.getBlockState(pos);
		if (blockState.getMaterial().isLiquid()) {
			return false;
		}
		if (!entity.getButterfly().isAcceptedEnvironment(entity.level, x, pos.getY(), z)) {
			return false;
		}

		entity.setDestination(null);
		entity.setState(EnumButterflyState.RESTING);
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		if (entity.getExhaustion() <= 0 && entity.canFly()) {
			return false;
		}
		return !entity.isInWater();
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void tick() {
		entity.changeExhaustion(-1);
	}

	private boolean canLand(BlockPos pos) {
		if (!entity.level.hasChunkAt(pos)) {
			return false;
		}
		BlockState blockState = entity.level.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!block.isAir(blockState, entity.level, pos)) {    //TODO
			//			if (!block.isPassable(entity.world, pos)) {
			return false;
		}
		if (isPlant(blockState)) {
			return true;
		}

		BlockState blockStateBelow = entity.level.getBlockState(pos.below());
		Block blockBelow = blockStateBelow.getBlock();
		return isRest(blockBelow) || blockBelow.is(BlockTags.LEAVES);
	}

	private static boolean isRest(Block block) {
		if (block instanceof FenceBlock) {
			return true;
		}
		return block instanceof WallBlock;
	}

	private static boolean isPlant(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof FlowerBlock) {
			return true;
		} else if (block instanceof IPlantable) {
			return true;
		} else if (block instanceof IGrowable) {
			return true;
		} else {
			return blockState.getMaterial() == Material.PLANT;
		}
	}
}
