/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import java.util.List;

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;

public class AlleleEffectRepulsion extends AlleleEffectThrottled {

	public AlleleEffectRepulsion(String uid) {
		super(uid, "repulsion", false, 100, true, true);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (isHalted(storedData, housing))
			return storedData;

		if (housing.getOwnerName() == null)
			return storedData;

		AxisAlignedBB hurtBox = getBounding(genome, housing, 1.0f);
		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityMob.class, hurtBox);

		for (Object obj : list) {
			EntityMob mob = (EntityMob) obj;

			for (Object objT : mob.tasks.taskEntries) {
				EntityAITaskEntry task = (EntityAITaskEntry) objT;
				if (task.action instanceof AIAvoidPlayers)
					return storedData;
			}

			mob.tasks.addTask(3, new AIAvoidPlayers(mob, 6.0f, 0.25f, 0.3f));
			mob.tasks.onUpdateTasks();

		}

		return storedData;
	}

}
