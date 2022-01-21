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

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AIAvoidPlayers extends Goal {

	private final PathfinderMob mob;
	private final PathNavigation pathNavigator;

	private final float farSpeed;
	private final float nearSpeed;
	private final float minDistance;

	@Nullable
	private Path path;

	@Nullable
	private Player player;

	public AIAvoidPlayers(PathfinderMob mob, float minDistance, float farSpeed, float nearSpeed) {
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

		if (!mob.getSensing().hasLineOfSight(player)) {
			return false;
		}

		Vec3 randomTarget = RandomPos.getPosAvoid(mob, 16, 7,
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
