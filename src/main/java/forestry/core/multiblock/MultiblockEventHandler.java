package forestry.core.multiblock;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * In your mod, subscribe this on both the client and server sides side to handle chunk
 * load events for your multiblock machines.
 * Chunks can load asynchronously in environments like MCPC+, so we cannot
 * process any blocks that are in chunks which are still loading.
 */
public class MultiblockEventHandler {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onChunkLoad(ChunkEvent.Load loadEvent) {
		Chunk chunk = loadEvent.getChunk();
		World world = loadEvent.world;
		MultiblockRegistry.onChunkLoaded(world, chunk.xPosition, chunk.zPosition);
	}

	// Cleanup, for nice memory usageness
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldUnload(WorldEvent.Unload unloadWorldEvent) {
		MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.world);
	}
}
