/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.lepidopterology.entities;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class AIButterflyFlee extends AIButterflyMovement {

    public AIButterflyFlee(EntityButterfly entity) {
        super(entity);
        setMutexFlags(EnumSet.of(Flag.MOVE));
        //		setMutexBits(3);	TODO mutex
    }

    @Override
    public boolean shouldExecute() {

        PlayerEntity player = entity.world.getClosestPlayer(
                entity,
                entity.getButterfly().getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getFlightDistance()
        );

        if (player == null || player.isSneaking()) {
            return false;
        }

        if (!entity.getEntitySenses().canSee(player)) {
            return false;
        }

        flightTarget = getRandomDestination();
        if (flightTarget == null) {
            return false;
        }

        if (player.getDistanceSq(flightTarget.x, flightTarget.y, flightTarget.z) < player.getDistance(entity)) {
            return false;
        }

        entity.setDestination(flightTarget);
        entity.setState(EnumButterflyState.FLYING);
        return true;
    }

}
