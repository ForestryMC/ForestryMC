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
package forestry.core.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.inventory.wrappers.ChestWrapper;
import forestry.core.utils.InventoryUtil;

public abstract class TileUtil {

	public static boolean isUsableByPlayer(EntityPlayer player, TileEntity tile) {
		if (tile == null) {
			return false;
		}
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

	public static <T extends TileEntity> T getTile(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			return null;
		}
	}

	public static IInventory getInventoryFromTile(TileEntity tile, ForgeDirection side) {
		if (!(tile instanceof IInventory)) {
			return null;
		}

		if (tile instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) tile;
			return new ChestWrapper(chest);
		}
		return InventoryUtil.getInventory((IInventory) tile, side);
	}
}
