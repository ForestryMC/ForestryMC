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

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class TileUtil {

	public static boolean isUsableByPlayer(EntityPlayer player, TileEntity tile) {
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();

		return !tile.isInvalid() &&
				world.getTileEntity(pos) == tile &&
				player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Nullable
	public static <T extends TileEntity> T getTile(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			return null;
		}
	}

	@Nullable
	public static IItemHandler getInventoryFromTile(@Nullable TileEntity tile, @Nullable EnumFacing side) {
		if (tile == null) {
			return null;
		}

		if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
			return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		}

		if (tile instanceof ISidedInventory) {
			return new SidedInvWrapper((ISidedInventory) tile, side);
		}

		if (tile instanceof IInventory) {
			return new InvWrapper((IInventory) tile);
		}

		return null;
	}
}
