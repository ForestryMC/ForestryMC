/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.utils.DamageSourceForestry;

public class AlleleEffectAggressive extends AlleleEffectThrottled {

	public static DamageSource damageSourceBeeAggressive = new DamageSourceForestry("bee.aggressive");

	public AlleleEffectAggressive(String uid) {
		super(uid, "aggressive", true, 40, false, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		if (isHalted(storedData, housing))
			return storedData;

		AxisAlignedBB hurtBox = getBounding(genome, housing, 1.0f);
		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, hurtBox);

		for (Object obj : list) {
			EntityLivingBase entity = (EntityLivingBase) obj;

			int damage = 4;

			// Players are not attacked if they wear a full set of apiarist's
			// armor.
			if (entity instanceof EntityPlayer) {
				int count = ItemArmorApiarist.wearsItems((EntityPlayer) entity, getUID(), true);
				// Full set, no damage/effect
				if (count > 3)
					continue;
				else if (count > 2)
					damage = 1;
				else if (count > 1)
					damage = 2;
				else if (count > 0)
					damage = 3;
			}

			entity.attackEntityFrom(damageSourceBeeAggressive, damage);
		}

		return storedData;
	}

}
