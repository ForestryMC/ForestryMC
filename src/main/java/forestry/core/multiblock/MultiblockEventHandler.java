package forestry.core.multiblock;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * In your mod, subscribe this on both the client and server sides side to handle chunk
 * load events for your multiblock machines.
 * Chunks can load asynchronously in environments like MCPC+, so we cannot
 * process any blocks that are in chunks which are still loading.
 */
public class MultiblockEventHandler {
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load loadEvent) {
		Chunk chunk = loadEvent.getChunk();
		World world = loadEvent.getWorld();
		MultiblockRegistry.onChunkLoaded(world, chunk.x, chunk.z);
	}

	// Cleanup, for nice memory usageness
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload unloadWorldEvent) {
		MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.getWorld());
	}
}
