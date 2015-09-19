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

import forestry.api.apiculture.IArmorApiarist.ArmorApiaristHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;

public class AlleleEffectCreeper extends AlleleEffectThrottled {

	private final int explosionChance = 50;
	byte defaultForce = 12;
	byte indexExplosionTimer = 1;
	byte indexExplosionForce = 2;

	public AlleleEffectCreeper(String uid) {
		super(uid, "creeper", true, 20, false, true);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (!(storedData instanceof EffectData))
			return new EffectData(3, 0);

		if (((EffectData) storedData).getIntSize() < 3)
			return new EffectData(3, 0);

		return storedData;
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isHalted(storedData, housing))
			return storedData;

		// If we are already triggered, we continue the explosion sequence.
		if (storedData.getInteger(indexExplosionTimer) > 0) {
			progressExplosion(storedData, world, housing.getXCoord(), housing.getYCoord(), housing.getZCoord());
			return storedData;
		}

		AxisAlignedBB infectionBox = getBounding(genome, housing, 1.0f);

		@SuppressWarnings("rawtypes")
		List list = world.getEntitiesWithinAABB(EntityPlayer.class, infectionBox);

		for (Object obj : list) {

			EntityPlayer player = (EntityPlayer) obj;

			int chance = explosionChance;
			storedData.setInteger(indexExplosionForce, defaultForce);

			// Players are not attacked if they wear a full set of apiarist's
			// armor.
			int count = ArmorApiaristHelper.wearsItems(player, getUID(), true);
			// Full set, no damage/effect
			if (count > 3)
				continue;
			else if (count > 2) {
				chance = 5;
				storedData.setInteger(indexExplosionForce, 6);
			} else if (count > 1) {
				chance = 20;
				storedData.setInteger(indexExplosionForce, 8);
			} else if (count > 0) {
				chance = 35;
				storedData.setInteger(indexExplosionForce, 10);
			}

			if (world.rand.nextInt(1000) >= chance)
				continue;

			world.playSoundEffect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord(), "mob.creeper", 4F,
					(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
			storedData.setInteger(indexExplosionTimer, 2); // Set explosion
			// timer
		}

		return storedData;
	}

	private void progressExplosion(IEffectData storedData, World world, int x, int y, int z) {

		int explosionTimer = storedData.getInteger(indexExplosionTimer);
		explosionTimer--;
		storedData.setInteger(indexExplosionTimer, explosionTimer);

		if (explosionTimer > 0)
			return;

		world.createExplosion(null, x, y, z, storedData.getInteger(indexExplosionForce), false);
	}

}
