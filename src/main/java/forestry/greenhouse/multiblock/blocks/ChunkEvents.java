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

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ChunkEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import forestry.greenhouse.multiblock.blocks.world.ChunkThread;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;

public class ChunkEvents {

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		if (GreenhouseBlockManager.getThread() == null || !GreenhouseBlockManager.getThread().isAlive()) {
			Thread climateThread = new Thread(new ChunkThread(false), "Forestry Greenhouse");
			GreenhouseBlockManager.setThread(climateThread, false);
			climateThread.start();
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		if (GreenhouseBlockManager.getClientThread() == null || !GreenhouseBlockManager.getClientThread().isAlive()) {
			Thread climateThread = new Thread(new ChunkThread(true), "Forestry Greenhouse Client");
			GreenhouseBlockManager.setThread(climateThread, true);
			climateThread.start();
		}
	}

	@SubscribeEvent
	public void loadChunk(ChunkEvent.Load event) {
		Chunk chunk = event.getChunk();
		World world = event.getWorld();
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		manager.loadChunk(world, chunk.x, chunk.z);
	}

	@SubscribeEvent
	public void unloadChunk(ChunkEvent.Unload event) {
		Chunk chunk = event.getChunk();
		World world = event.getWorld();
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		manager.unloadChunk(world, chunk.x, chunk.z);
	}

	@SubscribeEvent
	public void breakBlock(BreakEvent event) {
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		manager.markBlockDirty(world, pos);
	}

	@SubscribeEvent
	public void placeBlock(PlaceEvent event) {
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		manager.markBlockDirty(world, pos);
	}

	@SubscribeEvent
	public void multiPlaceBlock(MultiPlaceEvent event) {
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		World world = event.getWorld();
		for (BlockSnapshot snapshot : event.getReplacedBlockSnapshots()) {
			BlockPos pos = snapshot.getPos();
			manager.markBlockDirty(world, pos);
		}
	}

}
