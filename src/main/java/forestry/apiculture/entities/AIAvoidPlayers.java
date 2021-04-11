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
import java.util.EnumSet;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.vector.Vector3d;

public class AIAvoidPlayers extends Goal {

	private final CreatureEntity mob;
	private final PathNavigator pathNavigator;

	private final float farSpeed;
	private final float nearSpeed;
	private final float minDistance;

	@Nullable
	private Path path;

	@Nullable
	private PlayerEntity player;

	public AIAvoidPlayers(CreatureEntity mob, float minDistance, float farSpeed, float nearSpeed) {
		this.mob = mob;
		this.minDistance = minDistance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.pathNavigator = mob.getNavigation();
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {

		player = mob.level.getNearestPlayer(mob, minDistance);

		if (player == null) {
			return false;
		}

		if (!mob.getSensing().canSee(player)) {
			return false;
		}

		Vector3d randomTarget = RandomPositionGenerator.getPosAvoid(mob, 16, 7,
				player.position());

		if (randomTarget == null) {
			return false;
		}

		if (player.distanceToSqr(randomTarget.x, randomTarget.y, randomTarget.z) < player.distanceTo(mob)) {
			return false;
		}

		path = pathNavigator.createPath(randomTarget.x, randomTarget.y, randomTarget.z, 0);    //TODO what does the 4th param mean?
		return path != null;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.pathNavigator.isDone();
	}

	@Override
	public void start() {
		this.pathNavigator.moveTo(path, farSpeed);
	}

	@Override
	public void stop() {
		player = null;
	}

	@Override
	public void tick() {
		if (player != null && mob.distanceTo(player) < 49.0D) {
			mob.getNavigation().setSpeedModifier(nearSpeed);
		} else {
			mob.getNavigation().setSpeedModifier(farSpeed);
		}
	}
}
