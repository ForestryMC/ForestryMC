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
package forestry.apiculture.entities;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.Vec3d;

public class AIAvoidPlayers extends EntityAIBase {

	private final EntityCreature mob;
	private final PathNavigate pathNavigator;

	private final float farSpeed;
	private final float nearSpeed;
	private final float minDistance;

	@Nullable
	private Path path;

	@Nullable
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

		player = mob.world.getClosestPlayerToEntity(mob, minDistance);

		if (player == null) {
			return false;
		}

		if (!mob.getEntitySenses().canSee(player)) {
			return false;
		}

		Vec3d randomTarget = RandomPositionGenerator.findRandomTargetBlockAwayFrom(mob, 16, 7,
			new Vec3d(player.posX, player.posY, player.posZ));

		if (randomTarget == null) {
			return false;
		}

		if (player.getDistanceSq(randomTarget.x, randomTarget.y, randomTarget.z) < player.getDistance(mob)) {
			return false;
		}

		path = pathNavigator.getPathToXYZ(randomTarget.x, randomTarget.y, randomTarget.z);
		return path != null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.pathNavigator.noPath();
	}

	@Override
	public void startExecuting() {
		this.pathNavigator.setPath(path, farSpeed);
	}

	@Override
	public void resetTask() {
		player = null;
	}

	@Override
	public void updateTask() {
		if (player != null && mob.getDistance(player) < 49.0D) {
			mob.getNavigator().setSpeed(nearSpeed);
		} else {
			mob.getNavigator().setSpeed(farSpeed);
		}
	}
}
