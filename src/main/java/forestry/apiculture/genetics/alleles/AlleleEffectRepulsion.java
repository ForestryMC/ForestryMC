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
package forestry.apiculture.genetics.alleles;

import java.util.List;

import net.minecraft.entity.monster.MonsterEntity;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.entities.AIAvoidPlayers;

//import net.minecraft.entity.ai.goal.GoalSelector.EntityAITaskEntry;

public class AlleleEffectRepulsion extends AlleleEffectThrottled {

    public AlleleEffectRepulsion() {
        super("repulsion", false, 100, true, true);
    }

    @Override
    public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
        List<MonsterEntity> mobs = getEntitiesInRange(genome, housing, MonsterEntity.class);
        for (MonsterEntity mob : mobs) {
            if (!isMobAvoidingPlayers(mob)) {
                mob.goalSelector.addGoal(3, new AIAvoidPlayers(mob, 6.0f, 0.25f, 0.3f));
                mob.goalSelector.tick();    //TODO - I think
            }
        }

        return storedData;
    }

    private boolean isMobAvoidingPlayers(MonsterEntity mob) {
        mob.goalSelector.getRunningGoals().forEach(g -> {
            //TODO - hmm
            //			EntityAITaskEntry task = (EntityAITaskEntry) g;
            //			if (g instanceof AIAvoidPlayers) {
            //				return true;
            //			}
        });
        return false;
    }
}
