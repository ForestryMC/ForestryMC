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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import forestry.api.apiculture.BeeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.ReflectionHelper;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.proxy.Proxies;

public class AlleleEffectPotion extends AlleleEffectThrottled {

	private final Potion potion;
	private final int potionFXColor;
	private final boolean isBadEffect;
	private final int duration;
	private final float chance;

	public AlleleEffectPotion(String name, boolean isDominant, Potion potion, int duration, int throttle, float chance) {
		super(name, isDominant, throttle, true, false);
		this.potion = potion;
		this.isBadEffect = isBadEffect(potion);
		this.duration = duration;
		this.chance = chance;

		Collection<PotionEffect> potionEffects = Collections.singleton(new PotionEffect(potion.getId(), 1, 0));
		this.potionFXColor = PotionHelper.calcPotionLiquidColor(potionEffects);
	}

	public AlleleEffectPotion(String name, boolean isDominant, Potion potion, int duration) {
		this(name, isDominant, potion, duration, 200, 1.0f);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();
		AxisAlignedBB effectArea = getBounding(genome, housing);
		List list = housing.getWorld().getEntitiesWithinAABB(EntityPlayer.class, effectArea);

		for (Object entity : list) {
			if (!(entity instanceof EntityPlayer)) {
				continue;
			}

			if (world.rand.nextFloat() >= chance) {
				continue;
			}

			EntityPlayer player = (EntityPlayer) entity;

			int dur = this.duration;
			if (isBadEffect) {
				// Players are not attacked if they wear a full set of apiarist's armor.
				int count = BeeManager.armorApiaristHelper.wearsItems((EntityPlayer) entity, getUID(), true);
				if (count >= 4) {
					continue; // Full set, no damage/effect
				} else if (count == 3) {
					dur = this.duration / 4;
				} else if (count == 2) {
					dur = this.duration / 2;
				} else if (count == 1) {
					dur = this.duration * 3 / 4;
				}
			}

			player.addPotionEffect(new PotionEffect(potion.getId(), dur, 0));
		}

		return storedData;
	}

	//FIXME: remove when Potion.isBadEffect() is available server-side
	private static boolean isBadEffect(Potion potion) {
		try {
			return (Boolean) ReflectionHelper.getPrivateValue(Potion.class, potion, "field_76418_K", "isBadEffect");
		} catch (ReflectionHelper.UnableToFindFieldException e) {
			Proxies.log.severe("Could not access potion field isBadEffect.");
			return false;
		}
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextBoolean()) {
			super.doFX(genome, storedData, housing);
		} else {
			ChunkCoordinates coords = housing.getCoordinates();
			Proxies.common.addEntityPotionFX(housing.getWorld(), coords.posX + 0.5, coords.posY + 1, coords.posZ + 0.5, potionFXColor);
		}
		return storedData;
	}
}
