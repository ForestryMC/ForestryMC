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

public class AIButterflyRise extends AIButterflyMovement {

	public AIButterflyRise(EntityButterfly entity) {
		super(entity);
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (entity.getDestination() != null) {
			return false;
		}

		if (!entity.isCollidedHorizontally && entity.getRNG().nextInt(64) != 0) {
			return false;
		}

		flightTarget = getRandomDestinationUpwards();
		if (flightTarget == null) {
			if (entity.getState().doesMovement) {
				entity.setState(EnumButterflyState.HOVER);
			}
			return false;
		}

		entity.setDestination(flightTarget);
		entity.setState(EnumButterflyState.RISING);
		return true;
	}

	@Override
	public boolean continueExecuting() {
		if (entity.getState() != EnumButterflyState.RISING) {
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
		if (entity.getDestination().squareDistanceTo(entity.posX, entity.posY, entity.posZ) > 2.0f) {
			return true;
		}

		entity.setDestination(null);
		return false;
	}

	@Override
	public void updateTask() {
		if (entity.isInWater()) {
			flightTarget = getRandomDestinationUpwards();
		} else if (entity.isCollidedVertically && entity.getRNG().nextInt(62) == 0) {
			flightTarget = null;
		}

		entity.setDestination(flightTarget);
		entity.changeExhaustion(1);
	}
}
