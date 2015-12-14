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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.multiblock.IMultiblockComponent;

public class MultiblockUtil {
	/**
	 * Returns an array containing references to neighboring IMultiblockComponent tile entities.
	 * Primarily a utility method. Only works after tileentity construction.
	 *
	 * This method is chunk-safe on the server; it will not query for parts in chunks that are unloaded.
	 * Note that no method is chunk-safe on the client, because ChunkProviderClient is stupid.
	 * @return An array of references to neighboring IMultiblockComponent tile entities.
	 */
	public static List<IMultiblockComponent> getNeighboringParts(World world, IMultiblockComponent part) {
		ChunkCoordinates partCoord = part.getCoordinates();

		List<ChunkCoordinates> neighbors = new ArrayList<>(ForgeDirection.VALID_DIRECTIONS.length);
		for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
			ChunkCoordinates neighborCoord = new ChunkCoordinates(partCoord);
			neighborCoord.posX += forgeDirection.offsetX;
			neighborCoord.posY += forgeDirection.offsetY;
			neighborCoord.posZ += forgeDirection.offsetZ;
			neighbors.add(neighborCoord);
		}

		List<IMultiblockComponent> neighborParts = new ArrayList<>();
		IChunkProvider chunkProvider = world.getChunkProvider();
		for (ChunkCoordinates neighbor : neighbors) {
			if (!chunkProvider.chunkExists(neighbor.posX >> 4, neighbor.posZ >> 4)) {
				// Chunk not loaded, skip it.
				continue;
			}

			TileEntity te = world.getTileEntity(neighbor.posX, neighbor.posY, neighbor.posZ);
			if (te instanceof IMultiblockComponent) {
				neighborParts.add((IMultiblockComponent) te);
			}
		}
		return neighborParts;
	}
}
