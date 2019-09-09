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

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.DamageSourceForestry;

public class AlleleEffectAggressive extends AlleleEffectThrottled {
	private static final DamageSource damageSourceBeeAggressive = new DamageSourceForestry("bee.aggressive");

	public AlleleEffectAggressive() {
		super("aggressive", true, 40, false, false);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<LivingEntity> entities = getEntitiesInRange(genome, housing, LivingEntity.class);
		for (LivingEntity entity : entities) {
			int damage = 4;

			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, getRegistryName(), true);
			damage -= count;
			if (damage <= 0) {
				continue;
			}

			entity.attackEntityFrom(damageSourceBeeAggressive, damage);
		}

		return storedData;
	}

}
