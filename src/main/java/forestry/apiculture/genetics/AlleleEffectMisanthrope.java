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

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.utils.DamageSourceForestry;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class AlleleEffectMisanthrope extends AlleleEffectThrottled {

	public static final DamageSource damageSourceBeeEnd = new DamageSourceForestry("bee.end");

	public AlleleEffectMisanthrope(String uid) {
		super(uid, "misanthrope", true, 20, false, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		if (isHalted(storedData, housing))
			return storedData;

		AxisAlignedBB beatifyBox = getBounding(genome, housing, 1.0f);
		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityPlayer.class, beatifyBox);

		for (Object obj : list) {
			EntityPlayer player = (EntityPlayer) obj;

			int damage = 4;

			// Players are not attacked if they wear a full set of apiarist's
			// armor.
			int count = ItemArmorApiarist.wearsItems(player, getUID(), true);
			// Full set, no damage/effect
			if (count > 3)
				continue;
			else if (count > 2)
				damage = 1;
			else if (count > 1)
				damage = 2;
			else if (count > 0)
				damage = 3;

			player.attackEntityFrom(damageSourceBeeEnd, damage);
		}

		return storedData;
	}

}
