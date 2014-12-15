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

import forestry.lepidopterology.entities.EntityButterfly.EnumButterflyState;
import net.minecraft.entity.player.EntityPlayer;

public class AIButterflyFlee extends AIButterflyMovement {

	private EntityPlayer player;

	public AIButterflyFlee(EntityButterfly entity) {
		super(entity);
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {

		player = entity.worldObj.getClosestPlayerToEntity(entity, entity.getButterfly().getGenome().getPrimary().getFlightDistance());

		if (player == null || player.isSneaking())
			return false;

		if (!entity.getEntitySenses().canSee(player))
			return false;

		flightTarget = getRandomDestination();
		if (flightTarget == null)
			return false;

		if (player.getDistanceSq(flightTarget.posX, flightTarget.posY, flightTarget.posZ) < player.getDistanceSqToEntity(entity))
			return false;

		entity.setDestination(flightTarget);
		entity.setState(EnumButterflyState.FLYING);
		return true;
	}

}
