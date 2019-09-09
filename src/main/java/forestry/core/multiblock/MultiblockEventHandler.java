package forestry.core.multiblock;

import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * In your mod, subscribe this on both the client and server sides side to handle chunk
 * load events for your multiblock machines.
 * Chunks can load asynchronously in environments like MCPC+, so we cannot
 * process any blocks that are in chunks which are still loading.
 */
public class MultiblockEventHandler {
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load loadEvent) {
		IChunk chunk = loadEvent.getChunk();
		IWorld world = loadEvent.getWorld();
		//TODO - check right x part of chunk
		MultiblockRegistry.onChunkLoaded(world, chunk.getPos().getRegionCoordX(), chunk.getPos().getRegionCoordZ());
	}

	// Cleanup, for nice memory usageness
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload unloadWorldEvent) {
		MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.getWorld());
	}
}
