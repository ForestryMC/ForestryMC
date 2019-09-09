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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.render.ParticleRender;

public class AlleleEffectPotion extends AlleleEffectThrottled {

	private final Effect potion;
	private final int potionFXColor;
	private final int duration;
	private final float chance;

	public AlleleEffectPotion(String name, boolean isDominant, Effect potion, int duration, int throttle, float chance) {
		super(name, isDominant, throttle, true, false);
		this.potion = potion;
		this.duration = duration;
		this.chance = chance;

		Collection<EffectInstance> potionEffects = Collections.singleton(new EffectInstance(potion, 1, 0));
		this.potionFXColor = PotionUtils.getPotionColorFromEffectList(potionEffects);
	}

	public AlleleEffectPotion(String name, boolean isDominant, Effect potion, int duration) {
		this(name, isDominant, potion, duration, 200, 1.0f);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		World world = housing.getWorldObj();
		List<LivingEntity> entities = getEntitiesInRange(genome, housing, LivingEntity.class);
		for (LivingEntity entity : entities) {
			if (world.rand.nextFloat() >= chance) {
				continue;
			}

			int dur = this.duration;
			if (potion.getEffectType() == EffectType.HARMFUL) {
				// Entities are not attacked if they wear a full set of apiarist's armor.
				int count = BeeManager.armorApiaristHelper.wearsItems(entity, getRegistryName(), true);
				if (count >= 4) {
					continue; // Full set, no damage/effect
				} else if (count == 3) {
					dur = this.duration / 4;
				} else if (count == 2) {
					dur = this.duration / 2;
				} else if (count == 1) {
					dur = this.duration * 3 / 4;
				}
			} else {
				// don't apply positive effects to mobs
				if (entity instanceof IMob) {
					continue;
				}
			}

			entity.addPotionEffect(new EffectInstance(potion, dur, 0));
		}

		return storedData;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IEffectData doFX(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		World world = housing.getWorldObj();
		if (world.rand.nextBoolean()) {
			super.doFX(genome, storedData, housing);
		} else {
			Vec3d beeFXCoordinates = housing.getBeeFXCoordinates();
			ParticleRender.addEntityPotionFX(world, beeFXCoordinates.x, beeFXCoordinates.y + 0.5, beeFXCoordinates.z, potionFXColor);
		}
		return storedData;
	}
}
