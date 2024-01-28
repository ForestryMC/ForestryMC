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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class TileUtil {

	public static boolean isUsableByPlayer(Player player, BlockEntity tile) {
		BlockPos pos = tile.getBlockPos();
		Level world = tile.getLevel();

		return !tile.isRemoved() &&
				getTile(world, pos) == tile &&
				player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	/**
	 * Returns the tile at the specified position, returns null if it is the wrong type or does not exist.
	 * Avoids creating new tile entities when using a ChunkCache (off the main thread).
	 */
	@Nullable
	public static BlockEntity getTile(BlockGetter world, BlockPos pos) {
		if (world instanceof PathNavigationRegion chunkCache) {
			return chunkCache.getBlockEntity(pos);
		} else {
			return world.getBlockEntity(pos);
		}
	}

	/**
	 * Returns the tile of the specified class, returns null if it is the wrong type or does not exist.
	 * Avoids creating new tile entities when using a ChunkCache (off the main thread).
	 */
	@Nullable
	public static <T> T getTile(BlockGetter world, BlockPos pos, Class<T> tileClass) {
		BlockEntity tileEntity = getTile(world, pos);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			return null;
		}
	}

	@Nullable
	public static <T> T getTile(LootContext.Builder builder, Class<T> tileClass) {
		BlockEntity tileEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (tileClass.isInstance(tileEntity)) {
			return tileClass.cast(tileEntity);
		} else {
			return null;
		}
	}

	@Nullable
	public static <T> T getTile(BlockEntity tileEntity, Class<T> tileClass) {
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
	public static <T, R> R getResultFromTile(LevelReader world, BlockPos pos, Class<T> tileClass, ITileGetResult<T, R> tileGetResult) {
		T tile = getTile(world, pos, tileClass);
		if (tile != null) {
			return tileGetResult.getResult(tile);
		}
		return null;
	}

	/**
	 * Performs an {@link ITileGetResult} on a tile if the tile exists.
	 */
	@Nullable
	public static <T, R> R getResultFromTile(BlockEntity tileEntity, Class<T> tileClass, ITileGetResult<T, R> tileGetResult) {
		T tile = getTile(tileEntity, tileClass);
		if (tile != null) {
			return tileGetResult.getResult(tile);
		}
		return null;
	}

	@FunctionalInterface
	public interface ITileAction<T> {
		void actOnTile(T tile);
	}

	/**
	 * Performs an {@link ITileAction} on a tile if the tile exists.
	 */
	public static <T> void actOnTile(LevelReader world, BlockPos pos, Class<T> tileClass, ITileAction<T> tileAction) {
		T tile = getTile(world, pos, tileClass);
		if (tile != null) {
			tileAction.actOnTile(tile);
		}
	}

	@Nullable
	public static IItemHandler getInventoryFromTile(@Nullable BlockEntity tile, @Nullable Direction side) {
		if (tile == null) {
			return null;
		}


		LazyOptional<IItemHandler> itemCap = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, side);
		if (itemCap.isPresent()) {
			return itemCap.orElse(null);
		}


		if (tile instanceof WorldlyContainer) {
			return new SidedInvWrapper((WorldlyContainer) tile, side);
		}

		if (tile instanceof Container) {
			return new InvWrapper((Container) tile);
		}

		return null;
	}

	public static <T> LazyOptional<T> getInterface(Level world, BlockPos pos, Capability<T> capability, @Nullable Direction facing) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity == null) {
			return LazyOptional.empty();
		}
		return tileEntity.getCapability(capability, facing);
	}
}
