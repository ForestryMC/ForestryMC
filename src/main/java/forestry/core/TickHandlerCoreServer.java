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

import java.util.List;
import java.util.Random;

import com.google.common.collect.LinkedListMultimap;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.worldgen.WorldGenerator;
import forestry.plugins.PluginManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class TickHandlerCoreServer {

	private final WorldGenerator worldGenerator;
	private final LinkedListMultimap<Integer, ChunkCoords> chunkRegenList = LinkedListMultimap.create();

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
				for (IResupplyHandler handler : PluginManager.resupplyHandlers) {
					handler.resupply(player);
				}
			}
		}

		if (Config.doRetrogen) {
			World world = event.world;
			int dimensionID = world.provider.getDimension();
			List<ChunkCoords> chunkList = chunkRegenList.get(dimensionID);

			if (!chunkList.isEmpty()) {
				ChunkCoords coords = chunkList.get(0);
				chunkList.remove(0);

				// This bit is from FML's GameRegistry.generateWorld where the seed is constructed.
				long worldSeed = world.getSeed();
				Random random = new Random(worldSeed);
				long xSeed = random.nextLong() >> 2 + 1L;
				long zSeed = random.nextLong() >> 2 + 1L;
				random.setSeed(xSeed * coords.xCoord + zSeed * coords.zCoord ^ worldSeed);

				worldGenerator.retroGen(random, coords.xCoord, coords.zCoord, world);
			}
		}
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
				}
			}
		}
	}

	private static class ChunkCoords {
		public final int dimension;
		public final int xCoord;
		public final int zCoord;

		public ChunkCoords(Chunk chunk) {
			this.dimension = chunk.getWorld().provider.getDimension();
			this.xCoord = chunk.xPosition;
			this.zCoord = chunk.zPosition;
		}
	}

}
