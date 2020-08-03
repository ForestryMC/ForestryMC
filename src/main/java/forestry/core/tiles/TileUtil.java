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

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.Region;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public abstract class TileUtil {

    public static boolean isUsableByPlayer(PlayerEntity player, TileEntity tile) {
        BlockPos pos = tile.getPos();
        World world = tile.getWorld();

        return !tile.isRemoved() &&
                getTile(world, pos) == tile &&
                player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * Returns the tile at the specified position, returns null if it is the wrong type or does not exist.
     * Avoids creating new tile entities when using a ChunkCache (off the main thread).
     * see {@link FlowerPotBlock#getActualState(BlockState, IWorldReader, BlockPos)}
     */
    @Nullable
    public static TileEntity getTile(IBlockReader world, BlockPos pos) {
        if (world instanceof Region) {
            Region chunkCache = (Region) world;
            return chunkCache.getTileEntity(pos);
        } else {
            return world.getTileEntity(pos);
        }
    }

    /**
     * Returns the tile of the specified class, returns null if it is the wrong type or does not exist.
     * Avoids creating new tile entities when using a ChunkCache (off the main thread).
     * see {@link FlowerPotBlock#getActualState(BlockState, IWorldReader, BlockPos)}
     */
    @Nullable
    public static <T> T getTile(IBlockReader world, BlockPos pos, Class<T> tileClass) {
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
    public static <T, R> R getResultFromTile(IWorldReader world, BlockPos pos, Class<T> tileClass, ITileGetResult<T, R> tileGetResult) {
        T tile = getTile(world, pos, tileClass);
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
    public static <T> void actOnTile(IWorldReader world, BlockPos pos, Class<T> tileClass, ITileAction<T> tileAction) {
        T tile = getTile(world, pos, tileClass);
        if (tile != null) {
            tileAction.actOnTile(tile);
        }
    }

    @Nullable
    public static IItemHandler getInventoryFromTile(@Nullable TileEntity tile, @Nullable Direction side) {
        if (tile == null) {
            return null;
        }


        LazyOptional<IItemHandler> itemCap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (itemCap.isPresent()) {
            return itemCap.orElse(null);
        }


        if (tile instanceof ISidedInventory) {
            return new SidedInvWrapper((ISidedInventory) tile, side);
        }

        if (tile instanceof IInventory) {
            return new InvWrapper((IInventory) tile);
        }

        return null;
    }

    public static <T> LazyOptional<T> getInterface(World world, BlockPos pos, Capability<T> capability, @Nullable Direction facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) {
            return LazyOptional.empty();
        }
        return tileEntity.getCapability(capability, facing);
    }
}
