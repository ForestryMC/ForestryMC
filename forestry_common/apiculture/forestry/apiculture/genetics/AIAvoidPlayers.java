/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

public class AIAvoidPlayers extends EntityAIBase {

	private final EntityCreature mob;

	private final float farSpeed;
	private final float nearSpeed;
	private final float minDistance;

	private PathEntity pathing;

	private final PathNavigate pathNavigator;

	private EntityPlayer player;

	public AIAvoidPlayers(EntityCreature mob, float minDistance, float farSpeed, float nearSpeed) {
		this.mob = mob;
		this.minDistance = minDistance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.pathNavigator = mob.getNavigator();
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {

		player = mob.worldObj.getClosestPlayerToEntity(mob, minDistance);

		if (player == null)
			return false;

		if (!mob.getEntitySenses().canSee(player))
			return false;

		Vec3 randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 16, 7,
				Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

		if (randomTarget == null)
			return false;

		if (player.getDistanceSq(randomTarget.xCoord, randomTarget.yCoord, randomTarget.zCoord) < player.getDistanceSqToEntity(mob))
			return false;

		pathing = pathNavigator.getPathToXYZ(randomTarget.xCoord, randomTarget.yCoord, randomTarget.zCoord);
		return pathing == null ? false : pathing.isDestinationSame(randomTarget);
	}

	@Override
	public boolean continueExecuting() {
		return !this.pathNavigator.noPath();
	}

	@Override
	public void startExecuting() {
		this.pathNavigator.setPath(pathing, farSpeed);
	}

	@Override
	public void resetTask() {
		player = null;
	}

	@Override
	public void updateTask() {
		if (mob.getDistanceSqToEntity(player) < 49.0D)
			mob.getNavigator().setSpeed(nearSpeed);
		else
			mob.getNavigator().setSpeed(farSpeed);
	}
}
