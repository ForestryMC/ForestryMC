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
package forestry.apiculture.genetics;

import java.util.List;

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;

public class AlleleEffectRepulsion extends AlleleEffectThrottled {

	public AlleleEffectRepulsion() {
		super("repulsion", false, 100, true, true);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (isHalted(storedData, housing)) {
			return storedData;
		}

		if (housing.getOwner() == null) {
			return storedData;
		}

		AxisAlignedBB hurtBox = getBounding(genome, housing, 1.0f);
		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityMob.class, hurtBox);

		for (Object obj : list) {
			EntityMob mob = (EntityMob) obj;

			for (Object objT : mob.tasks.taskEntries) {
				EntityAITaskEntry task = (EntityAITaskEntry) objT;
				if (task.action instanceof AIAvoidPlayers) {
					return storedData;
				}
			}

			mob.tasks.addTask(3, new AIAvoidPlayers(mob, 6.0f, 0.25f, 0.3f));
			mob.tasks.onUpdateTasks();

		}

		return storedData;
	}

}
