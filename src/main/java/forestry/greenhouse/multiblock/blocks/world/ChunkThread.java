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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.core.utils.Log;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseChunk;

public class ChunkThread implements Runnable {

	private final long INTERVAL = 250L;
	private boolean stop = false;
	private boolean isClient;

	public ChunkThread(boolean isClient) {
		this.isClient = isClient;
	}

	@SideOnly(Side.CLIENT)
	private static void handleClientWorld() {
		handleDirtyChunks(Minecraft.getMinecraft().world);
	}

	private static void handleDirtyChunks(World world) {
		if (world == null || world.provider == null) {
			return;
		}
		GreenhouseBlockManager manager = GreenhouseBlockManager.getInstance();
		synchronized (manager) {
			List<Long> dirtyChunks = manager.getDirtyChunks(world);
			Iterator<Long> dirtyChunksIterator = dirtyChunks.iterator();

			while (dirtyChunksIterator.hasNext()) {
				Long chunkPos = dirtyChunksIterator.next();
				GreenhouseChunk chunk = manager.getChunk(world, chunkPos);
				if (chunk != null) {
					synchronized (chunk) {
						Collection<IGreenhouseProvider> providers = chunk.getDirtyProviders();
						Iterator<IGreenhouseProvider> dirtyProviders = providers.iterator();
						while (dirtyProviders.hasNext()) {
							IGreenhouseProvider provider = dirtyProviders.next();
							provider.recreate();

							dirtyProviders.remove();
						}
					}
				}
				dirtyChunksIterator.remove();
			}
			manager.tickUpdates(world);
		}
	}

	@Override
	public void run() {
		Log.info("Starting greenhouse thread");
		while (!this.stop) {
			try {
				long startTime = System.currentTimeMillis();
				if (isClient) {
					handleClientWorld();
				} else {
					for (World world : DimensionManager.getWorlds()) {
						handleDirtyChunks(world);
					}
				}
				long executionTime = System.currentTimeMillis() - startTime;
				try {
					if (executionTime > INTERVAL) {
						Log.warning("GREENHOUSE TAKING " + (executionTime - INTERVAL) + " ms LONGER THAN NORMAL");
					}
					Thread.sleep(Math.max(1L, INTERVAL - executionTime));
				} catch (InterruptedException e) {
				}
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Update Greenhouse change");
				throw new ReportedException(crashreport);
			}
		}
		Log.info("Stopping climate thread");
		GreenhouseBlockManager.setThread(null, isClient);
	}

}
