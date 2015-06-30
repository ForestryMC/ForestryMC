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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.vect.IVect;
import forestry.plugins.PluginApiculture;

public class AlleleEffectMiasmic extends AlleleEffectThrottled {

	private static final int infectionChance = 50;

	public AlleleEffectMiasmic() {
		super("miasmic", false, 50, false, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isHalted(storedData, housing)) {
			return storedData;
		}

		AxisAlignedBB infectionBox = getBounding(genome, housing, 1.0f);
		@SuppressWarnings("rawtypes")
		List list = world.getEntitiesWithinAABB(EntityPlayer.class, infectionBox);

		for (Object obj : list) {

			if (world.rand.nextInt(1000) >= infectionChance) {
				continue;
			}

			EntityPlayer player = (EntityPlayer) obj;

			int duration = 800;

			// Players are not attacked if they wear a full set of apiarist's
			// armor.
			int count = ItemArmorApiarist.wearsItems(player, getUID(), true);
			// Full set, no damage/effect
			if (count > 3) {
				continue;
			} else if (count > 2) {
				duration = 200;
			} else if (count > 1) {
				duration = 400;
			} else if (count > 0) {
				duration = 600;
			}

			player.addPotionEffect(new PotionEffect(Potion.poison.id, duration, 0));
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		IVect area = getModifiedArea(genome, housing);

		if (housing.getWorld().rand.nextBoolean()) {
			PluginApiculture.proxy.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), housing.getCoordinates(), genome.getPrimary().getIconColour(0), area);
		} else {
			PluginApiculture.proxy.addBeeHiveFX("particles/poison", housing.getWorld(), housing.getCoordinates(), 0xffffff, area);
		}
		return storedData;
	}

}
