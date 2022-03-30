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
import java.util.function.Consumer;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.ItemStackUtil;

import genetics.api.individual.IGenome;

public class AlleleEffectResurrection extends AlleleEffectThrottled {

	private static class Resurrectable<T extends Mob> {
		private final ItemStack res;
		private final EntityType<T> risen;
		private final Consumer<T> risenTransformer;

		private Resurrectable(ItemStack res, EntityType<T> risen) {
			this(res, risen, e -> {
			});
		}

		private Resurrectable(ItemStack res, EntityType<T> risen, Consumer<T> risenTransformer) {
			this.res = res;
			this.risen = risen;
			this.risenTransformer = risenTransformer;
		}


		private boolean spawnAndTransform(ItemEntity entity) {
			T spawnedEntity = EntityUtil.spawnEntity(entity.level, this.risen, entity.getX(), entity.getY(), entity.getZ());
			if (spawnedEntity != null) {
				this.risenTransformer.accept(spawnedEntity);
				return true;
			}
			return false;
		}
	}

	public static List<Resurrectable<? extends Mob>> getReanimationList() {
		ArrayList<Resurrectable<? extends Mob>> list = new ArrayList<>();
		list.add(new Resurrectable<>(new ItemStack(Items.BONE), EntityType.SKELETON));
		list.add(new Resurrectable<>(new ItemStack(Items.ARROW), EntityType.SKELETON));
		list.add(new Resurrectable<>(new ItemStack(Items.ROTTEN_FLESH), EntityType.ZOMBIE));
		list.add(new Resurrectable<>(new ItemStack(Items.BLAZE_ROD), EntityType.BLAZE));
		return list;
	}

	public static List<Resurrectable<? extends Mob>> getResurrectionList() {
		ArrayList<Resurrectable<?>> list = new ArrayList<>();
		list.add(new Resurrectable<>(new ItemStack(Items.GUNPOWDER), EntityType.CREEPER));
		list.add(new Resurrectable<>(new ItemStack(Items.ENDER_PEARL), EntityType.ENDERMAN));
		list.add(new Resurrectable<>(new ItemStack(Items.STRING), EntityType.SPIDER));
		list.add(new Resurrectable<>(new ItemStack(Items.SPIDER_EYE), EntityType.SPIDER));
		list.add(new Resurrectable<>(new ItemStack(Items.STRING), EntityType.CAVE_SPIDER));
		list.add(new Resurrectable<>(new ItemStack(Items.SPIDER_EYE), EntityType.CAVE_SPIDER));
		list.add(new Resurrectable<>(new ItemStack(Items.GHAST_TEAR), EntityType.GHAST));
		list.add(new Resurrectable<>(new ItemStack(Blocks.DRAGON_EGG), EntityType.ENDER_DRAGON, dragon -> dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN)));
		return list;
	}

	private final List<Resurrectable<? extends Mob>> resurrectables;

	public AlleleEffectResurrection(String name, List<Resurrectable<? extends Mob>> resurrectables) {
		super(name, true, 40, true, true);
		this.resurrectables = resurrectables;
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		List<ItemEntity> entities = getEntitiesInRange(genome, housing, ItemEntity.class);
		if (entities.isEmpty()) {
			return storedData;
		}

		Collections.shuffle(resurrectables);

		for (ItemEntity entity : entities) {
			if (resurrectEntity(entity)) {
				break;
			}
		}

		return storedData;
	}

	private boolean resurrectEntity(ItemEntity entity) {
		if (!entity.isAlive()) {
			return false;
		}

		ItemStack contained = entity.getItem();
		for (Resurrectable<? extends Mob> entry : resurrectables) {
			if (ItemStackUtil.isIdenticalItem(entry.res, contained)) {
				if (entry.spawnAndTransform(entity)) {
					contained.shrink(1);

					if (contained.getCount() <= 0) {
						entity.discard();
					}
				}

				return true;
			}
		}

		return false;
	}
}
