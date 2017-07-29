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
package forestry.greenhouse.multiblock.blocks;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.greenhouse.network.packets.PacketGreenhouseData;

/**
 * A cache that caches every block that is removed or added to a {@link IGreenhouseProvider}
 * to send it later to the client with the {@link PacketGreenhouseData}.
 */
public class GreenhouseBlockCache implements IStreamable {

	private HashMap<Long, Set<BlockPos>> positions;

	public GreenhouseBlockCache() {
		positions = new HashMap<>();
	}

	public void add(Long chunkPos, BlockPos position) {
		positions.computeIfAbsent(chunkPos, k -> new HashSet<>()).add(position);
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeInt(positions.size());
		for (Map.Entry<Long, Set<BlockPos>> entry : positions.entrySet()) {
			data.writeLong(entry.getKey());
			Set<BlockPos> chunkPositions = entry.getValue();
			data.writeInt(chunkPositions.size());
			for (BlockPos pos : chunkPositions) {
				data.writeBlockPos(pos);
			}
		}
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		positions.clear();
		int size = data.readInt();
		for (int i = 0; i < size; i++) {
			long chunkPosition = data.readLong();
			int chunkSize = data.readInt();
			Set<BlockPos> chunkPositions = new HashSet<>();
			for (int p = 0; p < chunkSize; p++) {
				chunkPositions.add(data.readBlockPos());
			}
			positions.put(chunkPosition, chunkPositions);
		}
	}

	public HashMap<Long, Set<BlockPos>> getPositions() {
		return positions;
	}

	public void clear() {
		positions.clear();
	}

}
