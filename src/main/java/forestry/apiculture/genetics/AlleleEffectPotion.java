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

import cpw.mods.fml.relauncher.ReflectionHelper;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.proxy.Proxies;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class AlleleEffectPotion extends AlleleEffectThrottled {

	private final Potion potion;
	private final boolean isBadEffect;
	private final int duration;

	public AlleleEffectPotion(String uid, String name, boolean isDominant, Potion potion, int duration, boolean requiresWorking) {
		super(uid, name, isDominant, 200, requiresWorking, false);
		this.potion = potion;
		this.isBadEffect = isBadEffect(potion);
		this.duration = duration;
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		if (isHalted(storedData, housing))
			return storedData;

		AxisAlignedBB beatifyBox = getBounding(genome, housing, 1.0f);
		List list = housing.getWorld().getEntitiesWithinAABB(EntityPlayer.class, beatifyBox);

		for (Object entity : list) {
			if (!(entity instanceof EntityPlayer))
				continue;

			EntityPlayer player = (EntityPlayer) entity;

			int duration = this.duration;
			if (isBadEffect) {
				// Players are not attacked if they wear a full set of apiarist's armor.
				int count = ItemArmorApiarist.wearsItems((EntityPlayer) entity, getUID(), true);
				if (count >= 4)
					continue; // Full set, no damage/effect
				else if (count == 3)
					duration = this.duration / 4;
				else if (count == 2)
					duration = this.duration / 2;
				else if (count == 1)
					duration = this.duration * 3 / 4;
			}

			player.addPotionEffect(new PotionEffect(potion.getId(), duration, 0));
		}

		return storedData;
	}

	//FIXME: remove when Potion.isBadEffect() is available server-side
	private static boolean isBadEffect(Potion potion) {
		try {
			return ReflectionHelper.getPrivateValue(Potion.class, potion, "field_76418_K", "isBadEffect");
		} catch (ReflectionHelper.UnableToFindFieldException e) {
			Proxies.log.severe("Could not access potion field isBadEffect.");
			return false;
		}
	}

}
