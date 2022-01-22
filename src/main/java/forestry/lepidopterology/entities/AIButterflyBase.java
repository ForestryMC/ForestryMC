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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public abstract class AIButterflyBase extends Goal {

	protected final EntityButterfly entity;

	protected AIButterflyBase(EntityButterfly entity) {
		this.entity = entity;
	}

	@Nullable
	protected Vec3 getRandomDestination() {
		if (entity.isInWater()) {
			return getRandomDestinationUpwards();
		}

		Vec3 entityPos = entity.position();
		Vec3 randomTarget = null; // RandomPos.getPosAvoid(entity, 16, 7, entityPos);

		if (randomTarget != null && validateDestination(randomTarget, false)) {
			return randomTarget;
		}
		return null;
	}

	@Nullable
	protected Vec3 getRandomDestinationUpwards() {
		Vec3 entityPos = entity.position();
		Vec3 destination = entityPos.add(0, entity.getRandom().nextInt(10) + 2, 0);
		if (validateDestination(destination, true)) {
			return destination;
		} else {
			return null;
		}
	}

	private boolean validateDestination(Vec3 dest, boolean allowFluids) {
		if (dest.y < 1) {
			return false;
		}
		BlockPos pos = new BlockPos(dest);
		if (!entity.level.hasChunkAt(pos)) {
			return false;
		}
		BlockState blockState = entity.level.getBlockState(pos);
		if (!allowFluids && blockState.getMaterial().isLiquid()) {
			return false;
		}
		//		if (!block.isPassable(entity.world, pos)) {
		if (!blockState.isAir()) {    //TODO
			return false;
		}
		return entity.getButterfly().isAcceptedEnvironment(entity.level, dest.x, dest.y, dest.z);
	}

}
