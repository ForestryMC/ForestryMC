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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.utils.WorldUtils;
import forestry.modules.ModuleManager;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class TickHandlerCoreServer {

	private static final LinkedListMultimap<ResourceKey<Level>, ChunkCoords> chunkRegenList = LinkedListMultimap.create();
	private static final Set<ResourceKey<Level>> checkForRetrogen = new HashSet<>();


	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		if (Config.enableBackpackResupply) {
			for (Player obj : event.level.players()) {
				for (IResupplyHandler handler : ModuleManager.resupplyHandlers) {
					handler.resupply(obj);
				}
			}
		}

		if (Config.doRetrogen && event.level instanceof ServerLevel world) {
			ResourceKey<Level> dimId = world.dimension();
			if (checkForRetrogen.contains(dimId)) {
				List<ChunkCoords> chunkList = chunkRegenList.get(dimId);
				Iterator<ChunkCoords> iterator = chunkList.iterator();
				while (iterator.hasNext()) {
					ChunkCoords coords = iterator.next();
					if (canDecorate(world, coords)) {
						iterator.remove();
						Random random = getRetrogenRandom(world, coords);
						//						worldGenerator.retroGen(random, coords.x, coords.z, world);
					}
				}
				checkForRetrogen.remove(dimId);
			}
		}
	}

	private static Random getRetrogenRandom(Level world, ChunkCoords coords) {
		long worldSeed = WorldUtils.asServer(world).getSeed();
		Random random = new Random(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed(xSeed * coords.x + zSeed * coords.z ^ worldSeed);
		return random;
	}

	private static boolean canDecorate(ServerLevel server, ChunkCoords chunkCoords) {
		ServerChunkCache chunkProvider = server.getChunkSource();
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				if (!chunkProvider.hasChunk(chunkCoords.x + x, chunkCoords.z + z)) {
					return false;
				}
			}
		}
		return true;
	}

	@SubscribeEvent
	public static void chunkSaveEventHandler(ChunkDataEvent.Save event) {
		CompoundTag tag = new CompoundTag();
		if (Config.doRetrogen) {
			tag.putBoolean("retrogen", true);
		}

		//TODO - correct?
		event.getData().put(Constants.MOD_ID, tag);
	}

	@SubscribeEvent
	public static void chunkLoadEventHandler(ChunkDataEvent.Load event) {
		if (Config.doRetrogen) {
			CompoundTag eventData = event.getData();
			if (eventData.contains(Constants.MOD_ID)) {
				CompoundTag tag = eventData.getCompound(Constants.MOD_ID);
				if (!tag.contains("retrogen") || Config.forceRetrogen) {
					ChunkCoords coords = new ChunkCoords(event.getChunk());
					chunkRegenList.put(coords.dimension, coords);
					checkForRetrogen.add(coords.dimension);
				}
			}
		}
	}

	private static class ChunkCoords {
		public final ResourceKey<Level> dimension;
		public final int x;
		public final int z;

		public ChunkCoords(ChunkAccess chunk) {
			LevelAccessor world = chunk.getWorldForge();
			if (world == null) {
				this.dimension = Level.OVERWORLD;
				this.x = 0;
				this.z = 0;
			} else {
				this.dimension = ((Level) world).dimension();
				this.x = chunk.getPos().x;
				this.z = chunk.getPos().z;
			}
		}
	}

}
