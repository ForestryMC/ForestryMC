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

import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.core.config.Constants;
import forestry.core.utils.MigrationHelper;

public abstract class TileUtil {

	public static void registerTile(Class<? extends TileEntity> tileClass, String key) {
		GameRegistry.registerTileEntity(tileClass, new ResourceLocation(Constants.MOD_ID, key));
		MigrationHelper.addTileName(key);
	}

	public static boolean isUsableByPlayer(EntityPlayer player, TileEntity tile) {
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();

		return !tile.isInvalid() &&
			getTile(world, pos) == tile &&
			player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	/**
	 * Returns the tile at the specified position, returns null if it is the wrong type or does not exist.
	 * Avoids creating new tile entities when using a ChunkCache (off the main thread).
	 * see {@link BlockFlowerPot#getActualState(IBlockState, IBlockAccess, BlockPos)}
	 */
	@Nullable
	public static TileEntity getTile(IBlockAccess world, BlockPos pos) {
		if (world instanceof ChunkCache) {
			ChunkCache chunkCache = (ChunkCache) world;
			return chunkCache.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
		} else {
			return world.getTileEntity(pos);
		}
	}

	/**
	 * Returns the tile of the specified class, returns null if it is the wrong type or does not exist.
	 * Avoids creating new tile entities when using a ChunkCache (off the main thread).
	 * see {@link BlockFlowerPot#getActualState(IBlockState, IBlockAccess, BlockPos)}
	 */
	@Nullable
	public static <T> T getTile(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
		TileEntity tileEntity = getTile(world, pos);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			return null;
		}
	}

	public interface ITileGetResult<T, R> {
		@Nullable
		R getResult(T tile);
	}

	/**
	 * Performs an {@link ITileGetResult} on a tile if the tile exists.
	 */
	@Nullable
	public static <T, R> R getResultFromTile(IBlockAccess world, BlockPos pos, Class<T> tileClass, ITileGetResult<T, R> tileGetResult) {
		T tile = getTile(world, pos, tileClass);
		if (tile != null) {
			return tileGetResult.getResult(tile);
		}
		return null;
	}

	public interface ITileAction<T> {
		void actOnTile(T tile);
	}

	/**
	 * Performs an {@link ITileAction} on a tile if the tile exists.
	 */
	public static <T> void actOnTile(IBlockAccess world, BlockPos pos, Class<T> tileClass, ITileAction<T> tileAction) {
		T tile = getTile(world, pos, tileClass);
		if (tile != null) {
			tileAction.actOnTile(tile);
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

	@Nullable
	public static <T> T getInterface(World world, BlockPos pos, Capability<T> capability, @Nullable EnumFacing facing) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null || !tileEntity.hasCapability(capability, facing)) {
			return null;
		}
		return tileEntity.getCapability(capability, facing);
	}
}
