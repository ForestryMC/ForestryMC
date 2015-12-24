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
package forestry.core.utils;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.EntityRegistry;

import forestry.api.core.ForestryAPI;
import forestry.core.entities.EntitySelector;

public abstract class EntityUtil {
	public static <E extends EntityLiving> E spawnEntity(World world, Class<E> entityClass, double x, double y, double z) {
		E entityLiving = createEntity(world, entityClass);
		if (entityLiving == null) {
			return null;
		}
		return spawnEntity(world, entityLiving, x, y, z);
	}

	public static <E extends EntityLiving> E spawnEntity(World world, E living, double x, double y, double z) {
		if (living == null) {
			return null;
		}

		living.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
		living.rotationYawHead = living.rotationYaw;
		living.renderYawOffset = living.rotationYaw;
		living.onSpawnWithEgg(null);
		world.spawnEntityInWorld(living);
		living.playLivingSound();
		return living;
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String ident, int id, int eggForeground, int eggBackground, int trackingRange, int updateFrequency, boolean sendVelocity) {
		EntityRegistry.registerModEntity(entityClass, ident, id, ForestryAPI.instance, trackingRange, updateFrequency, sendVelocity);
		Log.finer("Registered entity %s (%s) with id %s.", ident, entityClass.toString(), id);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> List<T> getEntitiesWithinAABB(World world, Class<T> entityClass, AxisAlignedBB boundingBox) {
		return world.getEntitiesWithinAABB(entityClass, boundingBox);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> List<T> selectEntitiesWithinAABB(World world, EntitySelector<T> entitySelector, AxisAlignedBB boundingBox) {
		return world.selectEntitiesWithinAABB(entitySelector.getEntityClass(), boundingBox, entitySelector);
	}

	private static <E extends EntityLiving> E createEntity(World world, Class<E> entityClass) {
		if (!EntityList.classToStringMapping.containsKey(entityClass)) {
			return null;
		}

		String entityString = (String) EntityList.classToStringMapping.get(entityClass);
		if (entityString == null) {
			return null;
		}

		Entity entity = EntityList.createEntityByName(entityString, world);
		return entityClass.cast(entity);
	}
}
