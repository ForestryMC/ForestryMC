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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.EntityRegistry;

import forestry.api.core.ForestryAPI;
import forestry.core.proxy.Proxies;

import buildcraft.api.tools.IToolWrench;

public class Utils {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");
	private static final Random rand = new Random();

	public static int getUID() {
		return rand.nextInt();
	}

	public static void dropInventory(IInventory inventory, World world, int x, int y, int z) {
		if (inventory == null) {
			return;
		}

		// Release inventory
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {

			ItemStack itemstack = inventory.getStackInSlot(slot);

			if (itemstack == null) {
				continue;
			}

			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

			while (itemstack.stackSize > 0) {
				int stackPartial = world.rand.nextInt(21) + 10;
				if (stackPartial > itemstack.stackSize) {
					stackPartial = itemstack.stackSize;
				}
				ItemStack drop = itemstack.splitStack(stackPartial);
				EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, drop);
				float accel = 0.05F;
				entityitem.motionX = (float) world.rand.nextGaussian() * accel;
				entityitem.motionY = (float) world.rand.nextGaussian() * accel + 0.2F;
				entityitem.motionZ = (float) world.rand.nextGaussian() * accel;
				world.spawnEntityInWorld(entityitem);
			}

			inventory.setInventorySlotContents(slot, null);
		}
	}

	public static boolean canWrench(EntityPlayer player, int x, int y, int z) {

		ItemStack itemstack = player.getCurrentEquippedItem();
		if (itemstack == null) {
			return false;
		}

		if (!(itemstack.getItem() instanceof IToolWrench)) {
			return false;
		}

		IToolWrench wrench = (IToolWrench) itemstack.getItem();
		return wrench.canWrench(player, x, y, z);
	}

	public static void useWrench(EntityPlayer player, int x, int y, int z) {
		ItemStack itemstack = player.getCurrentEquippedItem();

		if (itemstack == null) {
			return;
		}

		if (!(itemstack.getItem() instanceof IToolWrench)) {
			return;
		}

		((IToolWrench) itemstack.getItem()).wrenchUsed(player, x, y, z);
	}

	public static EnumTankLevel rateTankLevel(int scaled) {

		if (scaled < 5) {
			return EnumTankLevel.EMPTY;
		} else if (scaled < 30) {
			return EnumTankLevel.LOW;
		} else if (scaled < 60) {
			return EnumTankLevel.MEDIUM;
		} else if (scaled < 90) {
			return EnumTankLevel.HIGH;
		} else {
			return EnumTankLevel.MAXIMUM;
		}
	}

	public static boolean isWoodSlabBlock(Block block) {
		int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block));
		for (int oreId : oreIds) {
			if (oreId == slabWoodId) {
				return true;
			}
		}

		return false;
	}

	public static boolean isReplaceableBlock(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		return isReplaceableBlock(block);
	}

	public static boolean isReplaceableBlock(Block block) {
		return block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer
				|| block.getMaterial().isReplaceable();
	}

	public static boolean isUseableByPlayer(EntityPlayer player, TileEntity tile) {
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;
		World world = tile.getWorldObj();
		
		if (tile.isInvalid()) {
			return false;
		}
		
		if (world.getTileEntity(x, y, z) != tile) {
			return false;
		}

		return player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;

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
		Proxies.log.finer("Registered entity %s (%s) with id %s.", ident, entityClass.toString(), id);
	}

	public static <T extends TileEntity> T getTile(World world, int x, int y, int z, Class<T> tileClass) {
		T tileEntity = null;
		try {
			tileEntity = tileClass.cast(world.getTileEntity(x, y, z));
		} catch (ClassCastException ex) {
			Proxies.log.warning("Failed to cast a tile entity to a " + tileClass.getName() + " at " + x + '/' + y + '/' + z);
		}

		return tileEntity;
	}

	public static int addRGBComponents(int colour, int r, int g, int b) {
		r = ((colour & 0xff0000) >> 16) + r;
		g = ((colour & 0xff00) >> 8) + g;
		b = ((colour & 0xff)) + b;

		r = r <= 255 ? r : 255;
		g = g <= 255 ? g : 255;
		b = b <= 255 ? b : 255;

		return ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
	}

	public static int multiplyRGBComponents(int colour, float factor) {
		int r = (int) (((colour & 0xff0000) >> 16) * factor);
		int g = (int) (((colour & 0xff00) >> 8) * factor);
		int b = (int) (((colour & 0xff)) * factor);

		r = r <= 255 ? r : 255;
		g = g <= 255 ? g : 255;
		b = b <= 255 ? b : 255;

		return ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
	}
}
