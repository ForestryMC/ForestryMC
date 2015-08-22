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

import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.fml.common.registry.EntityRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ITileStructure;
import forestry.core.gadgets.TileForestry;
import forestry.core.proxy.Proxies;
import forestry.core.vect.Vect;

import buildcraft.api.tools.IToolWrench;

public class Utils {

	private static Random rand;

	public static int getUID() {
		if (rand == null) {
			rand = new Random();
		}

		return rand.nextInt();
	}

	public static void dropInventory(TileForestry tile, World world, BlockPos pos) {
		if (tile == null) {
			return;
		}

		// Release inventory
		if (tile instanceof ITileStructure) {

			IInventory inventory = ((ITileStructure) tile).getInventory();
			if (inventory != null) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (inventory.getStackInSlot(i) == null) {
						continue;
					}

					StackUtils.dropItemStackAsEntity(inventory.getStackInSlot(i), world, pos);
					inventory.setInventorySlotContents(i, null);
				}
			}
		} else {

			for (int slot = 0; slot < tile.getSizeInventory(); slot++) {

				ItemStack itemstack = tile.getStackInSlot(slot);

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
					EntityItem entityitem = new EntityItem(world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, drop);
					float accel = 0.05F;
					entityitem.motionX = (float) world.rand.nextGaussian() * accel;
					entityitem.motionY = (float) world.rand.nextGaussian() * accel + 0.2F;
					entityitem.motionZ = (float) world.rand.nextGaussian() * accel;
					world.spawnEntityInWorld(entityitem);

				}

				tile.setInventorySlotContents(slot, null);

			}
		}

	}

	public static IInventory getChest(IInventory inventory) {
		if (!(inventory instanceof TileEntityChest)) {
			return inventory;
		}

		TileEntityChest chest = (TileEntityChest) inventory;
		int xCoord  = chest.getPos().getX();
		int yCoord  = chest.getPos().getY();
		int zCoord  = chest.getPos().getZ();
		
		BlockPos[] adjacent = new BlockPos[]{new BlockPos(xCoord + 1, yCoord, zCoord), new BlockPos(xCoord - 1, yCoord, zCoord),
				new BlockPos(xCoord, yCoord, zCoord + 1), new BlockPos(xCoord, yCoord, zCoord - 1)};

		for (BlockPos pos : adjacent) {
			TileEntity otherchest = chest.getWorld().getTileEntity(pos);
			if (otherchest instanceof TileEntityChest) {
				return new InventoryLargeChest("", chest, (TileEntityChest) otherchest);
			}
		}

		return inventory;
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static int[] concat(int[] first, int[] second) {
		int[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static short[] concat(short[] first, short[] second) {
		short[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static float[] concat(float[] first, float[] second) {
		float[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
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
		if (!wrench.canWrench(player, pos)) {
			return false;
		}

		return true;
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

	public static boolean isReplaceableBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();

		return isReplaceableBlock(block);
	}

	public static boolean isReplaceableBlock(Block block) {
		return block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer
				|| block.getMaterial().isReplaceable();
	}

	public static boolean isLiquidBlock(World world, BlockPos pos) {
		return isLiquidBlock(world.getBlockState(pos).getBlock());
	}

	public static boolean isLiquidBlock(Block block) {
		return block == Blocks.water || block == Blocks.flowing_water || block == Blocks.lava || block == Blocks.flowing_lava;
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

	public static BiomeGenBase getBiomeAt(World world, BlockPos pos) {
		return world.getBiomeGenForCoordsBody(pos);
	}

	public static Entity spawnEntity(World world, Class<? extends Entity> entityClass, BlockPos pos) {
		if (!EntityList.classToStringMapping.containsKey(entityClass)) {
			return null;
		}

		return spawnEntity(world, EntityList.createEntityByName((String) EntityList.classToStringMapping.get(entityClass), world), pos);
	}

	public static Entity spawnEntity(World world, Entity spawn, BlockPos pos) {

		if (spawn != null && spawn instanceof EntityLiving) {

			EntityLiving living = (EntityLiving) spawn;
			spawn.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
			living.rotationYawHead = living.rotationYaw;
			living.renderYawOffset = living.rotationYaw;
			living.onSpawnFirstTime(null, null);
			world.spawnEntityInWorld(spawn);
			living.playLivingSound();
		}

		return spawn;
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String ident, int id, int eggForeground, int eggBackground, int trackingRange, int updateFrequency, boolean sendVelocity) {
		EntityRegistry.registerModEntity(entityClass, ident, id, ForestryAPI.instance, trackingRange, updateFrequency, sendVelocity);
		Proxies.log.finer("Registered entity %s (%s) with id %s.", ident, entityClass.toString(), id);
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

	public static String getFingerprint(Certificate certificate) {
		if (certificate == null) {
			return "Certificate invalid";
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] der = certificate.getEncoded();
			md.update(der);
			byte[] digest = md.digest();
			return tohex(digest);
		} catch (Exception ex) {
			return null;
		}
	}

	private static final String HEX = "0123456789abcdef";

	private static String tohex(byte[] checksum) {

		final StringBuilder hex = new StringBuilder(2 * checksum.length);
		for (byte bty : checksum) {
			hex.append(HEX.charAt((bty & 0xf0) >> 4)).append(HEX.charAt((bty & 0x0f)));
		}
		return hex.toString();
	}
}
