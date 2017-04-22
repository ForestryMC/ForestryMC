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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AlleleEffectResurrection extends AlleleEffectThrottled {

	public static class Resurrectable {
		public final ItemStack res;
		public final Class<? extends EntityLiving> risen;

		public Resurrectable(ItemStack res, Class<? extends EntityLiving> risen) {
			this.res = res;
			this.risen = risen;
		}
	}

	public static List<Resurrectable> getReanimationList() {
		ArrayList<Resurrectable> list = new ArrayList<>();
		list.add(new Resurrectable(new ItemStack(Items.BONE), EntitySkeleton.class));
		list.add(new Resurrectable(new ItemStack(Items.ARROW), EntitySkeleton.class));
		list.add(new Resurrectable(new ItemStack(Items.ROTTEN_FLESH), EntityZombie.class));
		list.add(new Resurrectable(new ItemStack(Items.BLAZE_ROD), EntityBlaze.class));
		return list;
	}

	public static List<Resurrectable> getResurrectionList() {
		ArrayList<Resurrectable> list = new ArrayList<>();
		list.add(new Resurrectable(new ItemStack(Items.GUNPOWDER), EntityCreeper.class));
		list.add(new Resurrectable(new ItemStack(Items.ENDER_PEARL), EntityEnderman.class));
		list.add(new Resurrectable(new ItemStack(Items.STRING), EntitySpider.class));
		list.add(new Resurrectable(new ItemStack(Items.SPIDER_EYE), EntitySpider.class));
		list.add(new Resurrectable(new ItemStack(Items.STRING), EntityCaveSpider.class));
		list.add(new Resurrectable(new ItemStack(Items.SPIDER_EYE), EntityCaveSpider.class));
		list.add(new Resurrectable(new ItemStack(Items.GHAST_TEAR), EntityGhast.class));
		list.add(new Resurrectable(new ItemStack(Blocks.DRAGON_EGG), EntityDragon.class));
		return list;
	}

	private final List<Resurrectable> resurrectables;

	public AlleleEffectResurrection(String name, List<Resurrectable> resurrectables) {
		super(name, true, 40, true, true);
		this.resurrectables = resurrectables;
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<EntityItem> entities = getEntitiesInRange(genome, housing, EntityItem.class);
		if (entities.isEmpty()) {
			return storedData;
		}

		Collections.shuffle(resurrectables);

		for (EntityItem entity : entities) {
			if (resurrectEntity(entity)) {
				break;
			}
		}

		return storedData;
	}

	private boolean resurrectEntity(EntityItem entity) {
		if (entity.isDead) {
			return false;
		}
		ItemStack contained = entity.getEntityItem();
		for (Resurrectable entry : resurrectables) {
			if (ItemStackUtil.isIdenticalItem(entry.res, contained)) {
				EntityUtil.spawnEntity(entity.world, entry.risen, entity.posX, entity.posY, entity.posZ);
				contained.shrink(1);
				if (contained.getCount() <= 0) {
					entity.setDead();
				}
				return true;
			}
		}
		return false;
	}
}
