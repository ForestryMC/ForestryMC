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
package forestry.core;

import com.google.common.collect.LinkedListMultimap;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.event.world.ChunkDataEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.worldgen.WorldGenerator;
import forestry.modules.ModuleManager;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class TickHandlerCoreServer {

	private final WorldGenerator worldGenerator;
	private final LinkedListMultimap<Integer, ChunkCoords> chunkRegenList = LinkedListMultimap.create();
	private final IntSet checkForRetrogen = new IntArraySet();

	public TickHandlerCoreServer(WorldGenerator worldGenerator) {
		this.worldGenerator = worldGenerator;
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (event.phase != Phase.END) {
			return;
		}

		if (Config.enableBackpackResupply) {
			for (Object obj : event.world.playerEntities) {
				EntityPlayer player = (EntityPlayer) obj;
				for (IResupplyHandler handler : ModuleManager.resupplyHandlers) {
					handler.resupply(player);
				}
			}
		}

		if (Config.doRetrogen && event.world instanceof WorldServer) {
			WorldServer world = (WorldServer) event.world;
			int dimensionID = world.provider.getDimension();
			if (checkForRetrogen.contains(dimensionID)) {
				List<ChunkCoords> chunkList = chunkRegenList.get(dimensionID);
				Iterator<ChunkCoords> iterator = chunkList.iterator();
				while (iterator.hasNext()) {
					ChunkCoords coords = iterator.next();
					if (canDecorate(world, coords)) {
						iterator.remove();
						Random random = getRetrogenRandom(world, coords);
						worldGenerator.retroGen(random, coords.x, coords.z, world);
					}
				}
				checkForRetrogen.remove(dimensionID);
			}
		}
	}

	/**
	 * This is from {@link GameRegistry#generateWorld(int, int, World, IChunkGenerator, IChunkProvider)} where the seed is constructed.
	 */
	private static Random getRetrogenRandom(World world, ChunkCoords coords) {
		long worldSeed = world.getSeed();
		Random random = new Random(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed(xSeed * coords.x + zSeed * coords.z ^ worldSeed);
		return random;
	}

	private static boolean canDecorate(WorldServer server, ChunkCoords chunkCoords) {
		ChunkProviderServer chunkProvider = server.getChunkProvider();
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				if (!chunkProvider.chunkExists(chunkCoords.x + x, chunkCoords.z + z)) {
					return false;
				}
			}
		}
		return true;
	}

	@SubscribeEvent
	public void chunkSaveEventHandler(ChunkDataEvent.Save event) {
		NBTTagCompound tag = new NBTTagCompound();
		if (Config.doRetrogen) {
			tag.setBoolean("retrogen", true);
		}

		event.getData().setTag(Constants.MOD_ID, tag);
	}

	@SubscribeEvent
	public void chunkLoadEventHandler(ChunkDataEvent.Load event) {
		if (Config.doRetrogen) {
			NBTTagCompound eventData = event.getData();
			if (eventData.hasKey(Constants.MOD_ID)) {
				NBTTagCompound tag = (NBTTagCompound) eventData.getTag(Constants.MOD_ID);
				if (!tag.hasKey("retrogen") || Config.forceRetrogen) {
					ChunkCoords coords = new ChunkCoords(event.getChunk());
					chunkRegenList.put(coords.dimension, coords);
					checkForRetrogen.add(coords.dimension);
				}
			}
		}
	}

	private static class ChunkCoords {
		public final int dimension;
		public final int x;
		public final int z;

		public ChunkCoords(Chunk chunk) {
			this.dimension = chunk.getWorld().provider.getDimension();
			this.x = chunk.x;
			this.z = chunk.z;
		}
	}

}
