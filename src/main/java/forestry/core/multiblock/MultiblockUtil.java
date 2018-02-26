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
package forestry.core.multiblock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.tiles.TileUtil;

public class MultiblockUtil {
	/**
	 * Returns an array containing references to neighboring IMultiblockComponent tile entities.
	 * Primarily a utility method. Only works after tileentity construction.
	 * <p>
	 * This method is chunk-safe on the server; it will not query for parts in chunks that are unloaded.
	 * Note that no method is chunk-safe on the client, because ChunkProviderClient is stupid.
	 *
	 * @return An array of references to neighboring IMultiblockComponent tile entities.
	 */
	public static List<IMultiblockComponent> getNeighboringParts(World world, IMultiblockComponent part) {
		BlockPos partCoord = part.getCoordinates();

		List<BlockPos> neighbors = new ArrayList<>(EnumFacing.values().length);
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos neighborCoord = new BlockPos(partCoord);
			neighborCoord = neighborCoord.offset(facing);
			neighbors.add(neighborCoord);
		}

		List<IMultiblockComponent> neighborParts = new ArrayList<>();
		IChunkProvider chunkProvider = world.getChunkProvider();
		for (BlockPos neighbor : neighbors) {
			if (chunkProvider.getLoadedChunk(neighbor.getX() >> 4, neighbor.getZ() >> 4) == null) {
				// Chunk not loaded, skip it.
				continue;
			}

			TileUtil.actOnTile(world, neighbor, IMultiblockComponent.class, neighborParts::add);
		}
		return neighborParts;
	}

	@Nullable
	public static <C extends IMultiblockComponent> C getComponent(IBlockAccess world, BlockPos pos, Class<C> componentClass) {
		return TileUtil.getTile(world, pos, componentClass);
	}

	@Nullable
	public static <C extends IMultiblockComponent, L extends IMultiblockLogic> L getLogic(IBlockAccess world, BlockPos pos, Class<C> componentClass) {
		C component = getComponent(world, pos, componentClass);
		if (component == null) {
			return null;
		}
		return (L) component.getMultiblockLogic();
	}

	@Nullable
	public static <C extends IMultiblockComponent, L extends IMultiblockLogic, M extends IMultiblockController> M getController(IBlockAccess world, BlockPos pos, Class<C> componentClass) {
		L logic = getLogic(world, pos, componentClass);
		if (logic == null || !logic.isConnected()) {
			return null;
		}
		return (M) logic.getController();
	}

}
