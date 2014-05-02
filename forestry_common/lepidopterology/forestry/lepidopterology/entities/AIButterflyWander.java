/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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

		if(entity.getDestination() != null)
			return false;

		flightTarget = getRandomDestination();
		if(flightTarget == null) {
			if(entity.getState().doesMovement)
				entity.setState(EnumButterflyState.HOVER);
			return false;
		}

		entity.setDestination(flightTarget);
		entity.setState(EnumButterflyState.FLYING);
		return true;
	}

	@Override
	public boolean continueExecuting() {
		if(entity.getState() != EnumButterflyState.FLYING)
			return false;
		if(flightTarget == null)
			return false;
		// Abort if the flight target changed on us.
		if(entity.getDestination() == null || !entity.getDestination().equals(flightTarget))
			return false;

		// Continue if we have not yet reached the destination.
		if(entity.getDestination().getDistanceSquared((int)entity.posX, (int)entity.posY, (int)entity.posZ) > 2.0f)
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
		if(entity.isInWater()) {
			flightTarget = getRandomDestinationUpwards();
			entity.setDestination(flightTarget);
		} if(entity.isCollided || entity.worldObj.rand.nextInt(300) == 0) {
			flightTarget = getRandomDestination();
			entity.setDestination(flightTarget);
		}

		entity.changeExhaustion(1);

	}
}
