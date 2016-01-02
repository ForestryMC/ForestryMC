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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.core.inventory.wrappers.ChestWrapper;
import forestry.core.utils.InventoryUtil;

public abstract class TileUtil {

	public static boolean isUsableByPlayer(EntityPlayer player, TileEntity tile) {
		if (tile == null) {
			return false;
		}
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

	public static <T extends TileEntity> T getTile(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			return null;
		}
	}

	public static IInventory getInventoryFromTile(TileEntity tile, EnumFacing side) {
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
