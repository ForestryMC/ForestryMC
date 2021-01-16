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
import net.minecraft.util.DamageSource;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.DamageSourceForestry;

public class AlleleEffectHeroic extends AlleleEffectThrottled {
	private static final DamageSource damageSourceBeeHeroic = new DamageSourceForestry("bee.heroic");

	public AlleleEffectHeroic() {
		super("heroic", false, 40, true, false);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<MonsterEntity> mobs = getEntitiesInRange(genome, housing, MonsterEntity.class);
		for (MonsterEntity mob : mobs) {
			mob.attackEntityFrom(damageSourceBeeHeroic, 2);
		}

		return storedData;
	}
}
