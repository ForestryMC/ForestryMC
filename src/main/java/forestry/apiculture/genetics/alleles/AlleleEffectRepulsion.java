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

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.entities.AIAvoidPlayers;

public class AlleleEffectRepulsion extends AlleleEffectThrottled {

	public AlleleEffectRepulsion() {
		super("repulsion", false, 100, true, true);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<EntityMob> mobs = getEntitiesInRange(genome, housing, EntityMob.class);
		for (EntityMob mob : mobs) {
			if (!isMobAvoidingPlayers(mob)) {
				mob.tasks.addTask(3, new AIAvoidPlayers(mob, 6.0f, 0.25f, 0.3f));
				mob.tasks.onUpdateTasks();
			}
		}

		return storedData;
	}

	private boolean isMobAvoidingPlayers(EntityMob mob) {
		for (Object objT : mob.tasks.taskEntries) {
			EntityAITaskEntry task = (EntityAITaskEntry) objT;
			if (task.action instanceof AIAvoidPlayers) {
				return true;
			}
		}
		return false;
	}
}
