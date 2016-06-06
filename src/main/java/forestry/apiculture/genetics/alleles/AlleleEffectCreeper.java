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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;

public class AlleleEffectCreeper extends AlleleEffectThrottled {

	private static final int explosionChance = 50;
	private static final byte defaultForce = 12;
	private static final byte indexExplosionTimer = 1;
	private static final byte indexExplosionForce = 2;

	public AlleleEffectCreeper() {
		super("creeper", true, 20, false, true);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (!(storedData instanceof EffectData)) {
			return new EffectData(3, 0);
		}

		if (((EffectData) storedData).getIntSize() < 3) {
			return new EffectData(3, 0);
		}

		return storedData;
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorldObj();
		BlockPos housingCoords = housing.getCoordinates();

		// If we are already triggered, we continue the explosion sequence.
		if (storedData.getInteger(indexExplosionTimer) > 0) {
			progressExplosion(storedData, world, housingCoords);
			return storedData;
		}

		List<EntityPlayer> players = getEntitiesInRange(genome, housing, EntityPlayer.class);
		for (EntityPlayer player : players) {
			int chance = explosionChance;
			storedData.setInteger(indexExplosionForce, defaultForce);

			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(player, getUID(), true);
			if (count > 3) {
				continue; // Full set, no damage/effect
			} else if (count > 2) {
				chance = 5;
				storedData.setInteger(indexExplosionForce, 6);
			} else if (count > 1) {
				chance = 20;
				storedData.setInteger(indexExplosionForce, 8);
			} else if (count > 0) {
				chance = 35;
				storedData.setInteger(indexExplosionForce, 10);
			}

			if (world.rand.nextInt(1000) >= chance) {
				continue;
			}

			float pitch = (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F;
			world.playSound(null, housingCoords.getX(), housingCoords.getY(), housingCoords.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, pitch);
			storedData.setInteger(indexExplosionTimer, 2); // Set explosion timer
		}

		return storedData;
	}

	private static void progressExplosion(IEffectData storedData, World world, BlockPos pos) {

		int explosionTimer = storedData.getInteger(indexExplosionTimer);
		explosionTimer--;
		storedData.setInteger(indexExplosionTimer, explosionTimer);

		if (explosionTimer > 0) {
			return;
		}

		world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), storedData.getInteger(indexExplosionForce), false);
	}

}
