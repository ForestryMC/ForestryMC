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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import forestry.greenhouse.api.greenhouse.IGreenhouseBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockManager;
import forestry.greenhouse.api.greenhouse.IGreenhouseChunk;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.core.utils.World2ObjectMap;
import forestry.greenhouse.multiblock.blocks.blank.BlankBlockHandler;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseChunk;
import forestry.greenhouse.multiblock.blocks.wall.WallBlockHandler;

public class GreenhouseBlockManager implements IGreenhouseBlockManager {
	private static final GreenhouseBlockManager INSTANCE = new GreenhouseBlockManager();
	private static Thread thread;
	private static Thread clientThread;

	private World2ObjectMap<ChunkManager> managers;

	private GreenhouseBlockManager() {
		managers = new World2ObjectMap(world -> new ChunkManager(world));
	}

	public static GreenhouseBlockManager getInstance() {
		return INSTANCE;
	}

	public static void setThread(Thread thread, boolean isClient) {
		if (isClient) {
			GreenhouseBlockManager.clientThread = thread;
		} else {
			GreenhouseBlockManager.thread = thread;
		}
	}

	public static Thread getClientThread() {
		return clientThread;
	}

	public static Thread getThread() {
		return thread;
	}

	@Override
	public BlankBlockHandler getBlankBlockHandler() {
		return BlankBlockHandler.getInstance();
	}

	@Override
	public WallBlockHandler getWallBlockHandler() {
		return WallBlockHandler.getInstance();
	}

	@Override
	@Nullable
	public IGreenhouseBlock getBlock(World world, BlockPos pos) {
		IGreenhouseChunk chunk = getChunk(world, pos.getX() >> 4, pos.getZ() >> 4);
		if (chunk != null) {
			return chunk.get(pos);
		}
		return null;
	}

	@Nullable
	public GreenhouseChunk getChunk(World world, long pos) {
		ChunkManager manager = managers.get(world);
		if (manager == null) {
			return null;
		}
		return manager.getChunk(pos);
	}

	@Override
	public IGreenhouseChunk getOrCreateChunk(World world, long chunkPos) {
		ChunkManager manager = managers.get(world);
		if (manager == null) {
			return null;
		}
		return manager.getOrCreateChunk(chunkPos);
	}

	@Nullable
	public GreenhouseChunk createChunk(World world, long pos) {
		ChunkManager manager = managers.get(world);
		if (manager == null) {
			return null;
		}
		return manager.createChunk(pos);
	}

	@Nullable
	public void markChunkDirty(World world, long pos) {
		ChunkManager manager = managers.get(world);
		if (manager != null) {
			manager.markChunkDirty(pos);
		}
	}

	@Nullable
	public synchronized List<Long> getDirtyChunks(World world) {
		ChunkManager manager = managers.get(world);
		if (manager == null) {
			return Collections.emptyList();
		}
		return manager.getDirtyChunks();
	}

	@Nullable
	public synchronized void markBlockDirty(World world, BlockPos pos) {
		long position = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
		GreenhouseChunk chunk = getChunk(world, position);
		if (chunk != null) {
			IGreenhouseBlock block = chunk.get(pos);
			if (block != null) {
				IGreenhouseProvider provider = block.getProvider();
				provider.onBlockChange();
				//markChunkDirty(world, position);
				//chunk.markProviderDirty(pos);

			}
		}
	}

	@Nullable
	public synchronized void markProviderDirty(World world, BlockPos pos, IGreenhouseProvider provider) {
		if (provider != null) {
			long position = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
			GreenhouseChunk chunk = getChunk(world, position);
			if (chunk != null) {
				markChunkDirty(world, position);
				chunk.markProviderDirty(provider);
			}
		}
	}

	@Nullable
	public void unloadChunk(World world, int x, int z) {
		ChunkManager manager = managers.get(world);
		if (manager != null) {
			manager.unload(x, z);
		}
	}

	@Nullable
	public void loadChunk(World world, int x, int z) {
		ChunkManager manager = managers.get(world);
		if (manager != null) {
			manager.load(x, z);
		}
	}

	public void scheduleUpdate(World world, BlockPos pos, IGreenhouseProvider provider, int delay) {
		ChunkManager manager = managers.get(world);
		if (manager != null) {
			manager.scheduleUpdate(pos, provider, delay);
		}
	}

	public void tickUpdates(World world) {
		ChunkManager manager = managers.get(world);
		if (manager != null) {
			manager.tickUpdates();
		}
	}
}