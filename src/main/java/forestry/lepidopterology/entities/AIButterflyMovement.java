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

import net.minecraft.util.BlockPos;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class AIButterflyMovement extends AIButterflyBase {

	protected BlockPos flightTarget;

	public AIButterflyMovement(EntityButterfly entity) {
		super(entity);
	}

	@Override
	public boolean continueExecuting() {
		if (entity.getState() != EntityButterfly.EnumButterflyState.FLYING) {
			return false;
		}
		if (flightTarget == null) {
			return false;
		}
		// Abort if the flight target changed on us.
		if (entity.getDestination() == null || !entity.getDestination().equals(flightTarget)) {
			return false;
		}

		// Continue if we have not yet reached the destination.
		if (getDistanceSquared(entity.getDestination(), entity.getPosition())> 2.0f) {
			return true;
		}

		entity.setDestination(null);
		return false;
	}
	
    public float getDistanceSquared(BlockPos pos, BlockPos posOther)
    {
        float f = (float)(pos.getX() - posOther.getX());
        float f1 = (float)(pos.getY() - posOther.getY());
        float f2 = (float)(pos.getZ() - posOther.getZ());
        return f * f + f1 * f1 + f2 * f2;
    }

	@Override
	public void updateTask() {
		// Reset destination if we did collide.
		if (entity.isInWater()) {
			flightTarget = getRandomDestinationUpwards();
		} else if (entity.isCollided) {
			flightTarget = entity.getRNG().nextBoolean() ? getRandomDestination() : null;
		} else if (entity.worldObj.rand.nextInt(300) == 0) {
			flightTarget = getRandomDestination();
		}
		entity.setDestination(flightTarget);
		entity.changeExhaustion(1);
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
		flightTarget = null;
	}
}
