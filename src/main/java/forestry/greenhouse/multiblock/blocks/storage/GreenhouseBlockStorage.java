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
package forestry.greenhouse.multiblock.blocks.storage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.greenhouse.IBlankBlock;
import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.api.greenhouse.IGreenhouseBlockStorage;
import forestry.api.greenhouse.IGreenhouseChunk;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.greenhouse.Position2D;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.greenhouse.multiblock.blocks.GreenhouseBlockCache;
import forestry.greenhouse.multiblock.blocks.client.ClientBlock;
import forestry.greenhouse.multiblock.blocks.client.ClientBlockHandler;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;

public class GreenhouseBlockStorage implements IGreenhouseBlockStorage, IStreamable {
	protected final HashMap<Long, HashMap<Position2D, IGreenhouseBlock>> blocks;
	protected final IGreenhouseProvider provider;
	protected final World world;
	protected final GreenhouseBlockCache cache;

	protected int blockCount;

	public GreenhouseBlockStorage(IGreenhouseProvider provider, World world) {
		this.blocks = new HashMap<>();
		this.world = world;
		this.provider = provider;
		this.cache = new GreenhouseBlockCache();
	}

	public void addProviderToChunks() {
		for (long chunkPos : blocks.keySet()) {
			IGreenhouseChunk chunk = getChunk(chunkPos);
			chunk.add(provider);
		}
	}

	public void removeProviderFromChunks() {
		for (long chunkPos : blocks.keySet()) {
			IGreenhouseChunk chunk = getChunk(chunkPos);
			chunk.remove(provider);
		}
	}

	@SideOnly(Side.CLIENT)
	public void createBlocksFromCache() {
		ClientBlockHandler blockHandler = ClientBlockHandler.getInstance();
		for (Set<BlockPos> positions : cache.getPositions().values()) {
			for (BlockPos position : positions) {
				ClientBlock block = blockHandler.createBlock(this, null, EnumFacing.DOWN, position);
				setBlock(position, block);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void createChunksFromCache() {
		removeProviderFromChunks();
		for (long chunkPos : cache.getPositions().keySet()) {
			IGreenhouseChunk chunk = getChunk(chunkPos);
			chunk.add(provider);
		}
	}

	public void clearBlocks(boolean chunkUnloading) {
		for (HashMap<Position2D, IGreenhouseBlock> blocks : this.blocks.values()) {
			Iterator<IGreenhouseBlock> blockIterator = blocks.values().iterator();
			while (blockIterator.hasNext()) {
				IGreenhouseBlock block = blockIterator.next();
				if (block != null) {
					removeBlock(block);
					blockIterator.remove();
				}
			}
		}
		blockCount = 0;
	}

	protected IGreenhouseChunk getChunk(BlockPos pos) {
		int xChunk = pos.getX() >> 4;
		int zChunk = pos.getZ() >> 4;
		return getChunk(ChunkPos.asLong(xChunk, zChunk));
	}

	protected IGreenhouseChunk getChunk(long chunkPos) {
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		return manager.getOrCreateChunk(world, chunkPos);
	}

	@Nullable
	@Override
	public IGreenhouseBlock getBlock(BlockPos pos) {
		long chunkPos = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
		HashMap<Position2D, IGreenhouseBlock> chunkBlocks = getChunkBlocks(chunkPos);
		return chunkBlocks.get(new Position2D(pos));
	}

	public boolean setBlock(BlockPos pos, @Nullable IGreenhouseBlock newBlock) {
		IGreenhouseChunk chunk = getChunk(pos);
		if (chunk != null) {
			IGreenhouseBlock oldBlock;
			Long chunkPos = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
			HashMap<Position2D, IGreenhouseBlock> chunkBlocks = getChunkBlocks(chunkPos);
			if (newBlock == null) {
				oldBlock = chunkBlocks.remove(new Position2D(pos));
			} else {
				oldBlock = chunkBlocks.put(new Position2D(pos), newBlock);
			}
			//Only count block on the server side
			if (!world.isRemote) {
				if (newBlock == null) {
					if (oldBlock instanceof IBlankBlock) {
						blockCount--;
					}
				} else if (newBlock != null) {
					cache.add(chunkPos, pos);
					if (newBlock instanceof IBlankBlock) {
						blockCount++;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int getBlockCount() {
		return blockCount;
	}

	public <B extends IGreenhouseBlock> void removeBlock(IGreenhouseBlock blockToRemove) {
		blockToRemove.getHandler().onRemoveBlock(this, blockToRemove);
	}

	@Override
	public IGreenhouseProvider getProvider() {
		return provider;
	}

	protected HashMap<Position2D, IGreenhouseBlock> getChunkBlocks(long chunkPos) {
		return blocks.computeIfAbsent(chunkPos, k -> new HashMap());
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		cache.writeData(data);
		cache.clear();
		data.writeInt(blockCount);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		cache.readData(data);
		blockCount = data.readInt();
	}
}
