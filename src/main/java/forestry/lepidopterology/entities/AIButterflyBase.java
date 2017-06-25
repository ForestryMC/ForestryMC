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

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AIButterflyBase extends EntityAIBase {

	protected final EntityButterfly entity;

	protected AIButterflyBase(EntityButterfly entity) {
		this.entity = entity;
	}

	@Nullable
	protected Vec3d getRandomDestination() {
		if (entity.isInWater()) {
			return getRandomDestinationUpwards();
		}

		Vec3d entityPos = new Vec3d(entity.posX, entity.posY, entity.posZ);
		Vec3d randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7, entityPos);

		if (randomTarget != null && validateDestination(randomTarget, false)) {
			return randomTarget;
		}
		return null;
	}

	@Nullable
	protected Vec3d getRandomDestinationUpwards() {
		Vec3d destination = new Vec3d(entity.posX, entity.posY + entity.getRNG().nextInt(10) + 2, entity.posZ);
		if (validateDestination(destination, true)) {
			return destination;
		} else {
			return null;
		}
	}

	private boolean validateDestination(Vec3d dest, boolean allowFluids) {
		if (dest.y < 1) {
			return false;
		}
		BlockPos pos = new BlockPos(dest);
		if (!entity.world.isBlockLoaded(pos)) {
			return false;
		}
		IBlockState blockState = entity.world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!allowFluids && blockState.getMaterial().isLiquid()) {
			return false;
		}
		if (!block.isPassable(entity.world, pos)) {
			return false;
		}
		return entity.getButterfly().isAcceptedEnvironment(entity.world, dest.x, dest.y, dest.z);
	}

}
