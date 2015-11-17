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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.proxy.Proxies;

public class AlleleEffectIgnition extends AlleleEffectThrottled {
	private static final int ignitionChance = 50;
	private static final int fireDuration = 500;

	public AlleleEffectIgnition() {
		super("ignition", false, 20, false, true);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		World world = housing.getWorld();
		List<EntityLivingBase> entities = getEntitiesInRange(genome, housing, EntityLivingBase.class);
		for (EntityLivingBase entity : entities) {
			int chance = ignitionChance;
			int duration = fireDuration;

			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, getUID(), true);
			if (count > 3) {
				continue; // Full set, no damage/effect
			} else if (count > 2) {
				chance = 5;
				duration = 50;
			} else if (count > 1) {
				chance = 20;
				duration = 200;
			} else if (count > 0) {
				chance = 35;
				duration = 350;
			}

			if (world.rand.nextInt(1000) >= chance) {
				continue;
			}

			entity.setFire(duration);
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextInt(2) != 0) {
			super.doFX(genome, storedData, housing);
		} else {
			Vec3 beeFXCoordinates = getFXCoordinates(housing);
			Proxies.render.addEntityIgnitionFX(housing.getWorld(), beeFXCoordinates.xCoord, beeFXCoordinates.yCoord + 0.5, beeFXCoordinates.zCoord);
		}
		return storedData;
	}

}
