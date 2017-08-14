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
package forestry.greenhouse.multiblock.blocks.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseChunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

class ChunkManager {
	public final Map<Long, GreenhouseChunk> id2ChunkMap = new Long2ObjectOpenHashMap(8192);
	public final List<Long> dirtyChunks = new ArrayList<>();
	private final Set<NextTickEntry> pendingTickEntries = Sets.newHashSet();
	private final TreeSet<NextTickEntry> pendingTickEntriesSorted = new TreeSet();
	private final List<NextTickEntry> pendingTickEntriesThisTick = Lists.newArrayList();
	private final World world;

	public ChunkManager(World world) {
		this.world = world;
	}

	public void markChunkDirty(long pos) {
		dirtyChunks.add(pos);
	}

	public List<Long> getDirtyChunks() {
		return dirtyChunks;
	}

	public GreenhouseChunk createChunk(long pos) {
		GreenhouseChunk chunk = new GreenhouseChunk();
		id2ChunkMap.put(pos, chunk);
		return chunk;
	}

	public GreenhouseChunk getOrCreateChunk(long pos) {
		GreenhouseChunk chunk = getChunk(pos);
		if (chunk == null) {
			chunk = createChunk(pos);
		}
		return chunk;
	}

	public GreenhouseChunk getChunk(long pos) {
		return id2ChunkMap.get(pos);
	}

	public void load(int x, int z) {
		long chunkPos = ChunkPos.asLong(x, z);
		updateChunk(x + 1, z, chunkPos);
		updateChunk(x - 1, z, chunkPos);
		updateChunk(x, z + 1, chunkPos);
		updateChunk(x, z - 1, chunkPos);
	}

	public void unload(int x, int z) {
		long pos = ChunkPos.asLong(x, z);
		GreenhouseChunk chunk = id2ChunkMap.remove(pos);
		if (chunk != null) {
			for (IGreenhouseProvider manager : chunk.getProviders()) {
				manager.onUnloadChunk(pos);
			}
		}
	}

	private void updateChunk(int x, int z, long updatedChunk) {
		GreenhouseChunk chunk = getChunk(ChunkPos.asLong(x, z));
		if (chunk != null) {
			for (IGreenhouseProvider manager : chunk.getProviders()) {
				if (manager.hasUnloadedChunks()) {
					manager.onLoadChunk(updatedChunk);
				}
			}
		}
	}

	public void scheduleUpdate(BlockPos pos, IGreenhouseProvider provider, int delay) {
		NextTickEntry entry = new NextTickEntry(pos, provider);

		entry.setScheduledTime((long) delay + getWorldTotalTime());

		if (!this.pendingTickEntries.contains(entry)) {
			this.pendingTickEntries.add(entry);
			this.pendingTickEntriesSorted.add(entry);
		}
	}

	public void tickUpdates() {
		int entriesCount = this.pendingTickEntriesSorted.size();

		if (entriesCount != this.pendingTickEntries.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {

			for (int entryCount = 0; entryCount < entriesCount; ++entryCount) {
				NextTickEntry entry = pendingTickEntriesSorted.first();

				if (entry.scheduledTime > getWorldTotalTime()) {
					break;
				}

				this.pendingTickEntriesSorted.remove(entry);
				this.pendingTickEntries.remove(entry);
				this.pendingTickEntriesThisTick.add(entry);
			}

			Iterator<NextTickEntry> iterator = this.pendingTickEntriesThisTick.iterator();

			while (iterator.hasNext()) {
				NextTickEntry entry = iterator.next();
				iterator.remove();

				if (world.isBlockLoaded(entry.getPosition())) {
					entry.getProvider().scheduledUpdate();
				} else {
					scheduleUpdate(entry.getPosition(), entry.getProvider(), 0);
				}
			}

			this.pendingTickEntriesThisTick.clear();
		}
	}

	private long getWorldTotalTime() {
		return world.getWorldInfo().getWorldTotalTime();
	}
}
