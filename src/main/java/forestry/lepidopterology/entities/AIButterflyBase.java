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
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public abstract class AIButterflyBase extends EntityAIBase {

	protected final EntityButterfly entity;

	protected AIButterflyBase(EntityButterfly entity) {
		this.entity = entity;
	}

	protected Vec3 getRandomDestination() {
		if (entity.isInWater()) {
			return getRandomDestinationUpwards();
		}

		Vec3 randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7,
				Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));

		if (randomTarget == null) {
			return null;
		}

		if (validateDestination(randomTarget, false)) {
			return randomTarget;
		} else {
			return null;
		}
	}

	protected Vec3 getRandomDestinationUpwards() {
		Vec3 destination = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getRNG().nextInt(10) + 2, entity.posZ);
		if (validateDestination(destination, true)) {
			return destination;
		} else {
			return null;
		}
	}

	private boolean validateDestination(Vec3 dest, boolean allowFluids) {
		if (dest.yCoord < 1) {
			return false;
		}
		Block block = entity.worldObj.getBlock((int) dest.xCoord, (int) dest.yCoord, (int) dest.zCoord);
		if (!allowFluids && block.getMaterial().isLiquid()) {
			return false;
		}
		// getBlocksMovement is a bad name, getAllowsMovement would be a better name.
		if (!block.getBlocksMovement(entity.worldObj, (int) dest.xCoord, (int) dest.yCoord, (int) dest.zCoord)) {
			return false;
		}
		return entity.getButterfly().isAcceptedEnvironment(entity.worldObj, dest.xCoord, dest.yCoord, dest.zCoord);
	}

}
