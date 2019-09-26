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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.worldgen.WorldFeatureManager;
import forestry.modules.ModuleManager;

public class TickHandlerCoreServer {

	private final WorldFeatureManager worldGenerator;
	private final LinkedListMultimap<ResourceLocation, ChunkCoords> chunkRegenList = LinkedListMultimap.create();
	private final Set<ResourceLocation> checkForRetrogen = new HashSet<>();

	public TickHandlerCoreServer(WorldFeatureManager worldGenerator) {
		this.worldGenerator = worldGenerator;
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		if (Config.enableBackpackResupply) {
			for (PlayerEntity obj : event.world.getPlayers()) {
				for (IResupplyHandler handler : ModuleManager.resupplyHandlers) {
					handler.resupply(obj);
				}
			}
		}

		if (Config.doRetrogen && event.world instanceof ServerWorld) {
			ServerWorld world = (ServerWorld) event.world;
			ResourceLocation dimId = DimensionType.getKey(world.dimension.getType());
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

	/**
	 * This is from {@link GameRegistry#generateWorld(int, int, World, ChunkGenerator, AbstractChunkProvider)} where the seed is constructed.
	 */
	private static Random getRetrogenRandom(World world, ChunkCoords coords) {
		long worldSeed = world.getSeed();
		Random random = new Random(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed(xSeed * coords.x + zSeed * coords.z ^ worldSeed);
		return random;
	}

	private static boolean canDecorate(ServerWorld server, ChunkCoords chunkCoords) {
		ServerChunkProvider chunkProvider = server.getChunkProvider();
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
		CompoundNBT tag = new CompoundNBT();
		if (Config.doRetrogen) {
			tag.putBoolean("retrogen", true);
		}

		//TODO - correct?
		event.getData().put(Constants.MOD_ID, tag);
	}

	@SubscribeEvent
	public void chunkLoadEventHandler(ChunkDataEvent.Load event) {
		if (Config.doRetrogen) {
			CompoundNBT eventData = event.getData();
			if (eventData.contains(Constants.MOD_ID)) {
				CompoundNBT tag = eventData.getCompound(Constants.MOD_ID);
				if (!tag.contains("retrogen") || Config.forceRetrogen) {
					ChunkCoords coords = new ChunkCoords(event.getChunk());
					chunkRegenList.put(coords.dimension, coords);
					checkForRetrogen.add(coords.dimension);
				}
			}
		}
	}

	private static class ChunkCoords {
		public final ResourceLocation dimension;
		public final int x;
		public final int z;

		public ChunkCoords(IChunk chunk) {
			IWorld world = chunk.getWorldForge();
			if (world == null) { //TODO
				this.dimension = new ResourceLocation("error", "error");
				this.x = 0;
				this.z = 0;
			} else {
				this.dimension = DimensionType.getKey(world.getDimension().getType());
				this.x = chunk.getPos().x;
				this.z = chunk.getPos().z;
			}
		}
	}

}
