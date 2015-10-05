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
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ForestryAPI;
import forestry.core.circuits.ISocketable;
import forestry.core.proxy.Proxies;

import buildcraft.api.tools.IToolWrench;

public abstract class Utils {

	private static final int slabWoodId = OreDictionary.getOreID("slabWood");
	private static final Random rand = new Random();

	public static int getUID() {
		return rand.nextInt();
	}

	public static void dropInventory(IInventory inventory, World world, BlockPos pos) {
		if (inventory == null) {
			return;
		}

		// Release inventory
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack itemstack = inventory.getStackInSlot(slot);
			dropItemStackFromInventory(itemstack, world, pos);
			inventory.setInventorySlotContents(slot, null);
		}
	}

	public static void dropSockets(ISocketable socketable, World world, BlockPos pos) {
		for (int slot = 0; slot < socketable.getSocketCount(); slot++) {
			ItemStack itemstack = socketable.getSocket(slot);
			dropItemStackFromInventory(itemstack, world, pos);
			socketable.setSocket(slot, null);
		}
	}

	public static void dropItemStackFromInventory(ItemStack itemStack, World world, BlockPos pos) {
		if (itemStack == null) {
			return;
		}

		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

		while (itemStack.stackSize > 0) {
			int stackPartial = world.rand.nextInt(21) + 10;
			if (stackPartial > itemStack.stackSize) {
				stackPartial = itemStack.stackSize;
			}
			ItemStack drop = itemStack.splitStack(stackPartial);
			EntityItem entityitem = new EntityItem(world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, drop);
			float accel = 0.05F;
			entityitem.motionX = (float) world.rand.nextGaussian() * accel;
			entityitem.motionY = (float) world.rand.nextGaussian() * accel + 0.2F;
			entityitem.motionZ = (float) world.rand.nextGaussian() * accel;
			world.spawnEntityInWorld(entityitem);
		}
	}
	
	public static boolean canWrench(EntityPlayer player, BlockPos pos) {

		ItemStack itemstack = player.getCurrentEquippedItem();
		if (itemstack == null) {
			return false;
		}

		if (!(itemstack.getItem() instanceof IToolWrench)) {
			return false;
		}

		IToolWrench wrench = (IToolWrench) itemstack.getItem();
		return wrench.canWrench(player, pos);
	}

	public static void useWrench(EntityPlayer player, BlockPos pos) {
		ItemStack itemstack = player.getCurrentEquippedItem();

		if (itemstack == null) {
			return;
		}

		if (!(itemstack.getItem() instanceof IToolWrench)) {
			return;
		}

		((IToolWrench) itemstack.getItem()).wrenchUsed(player, pos);
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
		if(block == Blocks.air)
			return false;
		int[] oreIds = OreDictionary.getOreIDs(new ItemStack(block));
		for (int oreId : oreIds) {
			if (oreId == slabWoodId) {
				return true;
			}
		}

		return false;
	}

	public static boolean isReplaceableBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		return isReplaceableBlock(block);
	}

	public static boolean isReplaceableBlock(Block block) {
		return block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer
				|| block.getMaterial().isReplaceable();
	}

	public static boolean isUseableByPlayer(EntityPlayer player, TileEntity tile) {
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();
		
		if (tile.isInvalid()) {
			return false;
		}
		
		if (world.getTileEntity(pos) != tile) {
			return false;
		}

		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;

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
		living.onSpawnFirstTime(null, null);
		world.spawnEntityInWorld(living);
		living.playLivingSound();
		return living;
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String ident, int id, int eggForeground, int eggBackground, int trackingRange, int updateFrequency, boolean sendVelocity) {
		EntityRegistry.registerModEntity(entityClass, ident, id, ForestryAPI.instance, trackingRange, updateFrequency, sendVelocity);
		Proxies.log.finer("Registered entity %s (%s) with id %s.", ident, entityClass.toString(), id);
	}

	public static <T extends TileEntity> T getTile(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			Proxies.log.warning("Failed to cast a tile entity {" + tileEntity + "} to a {" + tileClass.getName() + "} at " + pos.getX() + '/' + pos.getY() + '/' + pos.getZ());
			return null;
		}
	}

	public static boolean isIndexInRange(int index, int start, int count) {
		return (index >= start) && (index < start + count);
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
