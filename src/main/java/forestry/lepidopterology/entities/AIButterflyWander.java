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

import net.minecraft.util.ChunkCoordinates;

import forestry.lepidopterology.entities.EntityButterfly.EnumButterflyState;

public class AIButterflyWander extends AIButterflyBase {

	private ChunkCoordinates flightTarget;

	public AIButterflyWander(EntityButterfly entity) {
		super(entity);
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {

		if (entity.getDestination() != null)
			return false;

		flightTarget = getRandomDestination();
		if (flightTarget == null) {
			if (entity.getState().doesMovement)
				entity.setState(EnumButterflyState.HOVER);
			return false;
		}

		entity.setDestination(flightTarget);
		entity.setState(EnumButterflyState.FLYING);
		return true;
	}

	@Override
	public boolean continueExecuting() {
		if (entity.getState() != EnumButterflyState.FLYING)
			return false;
		if (flightTarget == null)
			return false;
		// Abort if the flight target changed on us.
		if (entity.getDestination() == null || !entity.getDestination().equals(flightTarget))
			return false;

		// Continue if we have not yet reached the destination.
		if (entity.getDestination().getDistanceSquared((int) entity.posX, (int) entity.posY, (int) entity.posZ) > 2.0f)
			return true;

		entity.setDestination(null);
		return false;
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
		flightTarget = null;
	}

	@Override
	public void updateTask() {
		// Reset destination if we did collide.
		if (entity.isInWater()) {
			flightTarget = getRandomDestinationUpwards();
			entity.setDestination(flightTarget);
		}
		if (entity.isCollided || entity.worldObj.rand.nextInt(300) == 0) {
			flightTarget = getRandomDestination();
			entity.setDestination(flightTarget);
		}

		entity.changeExhaustion(1);

	}
}
