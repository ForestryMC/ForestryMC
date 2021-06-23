package forestry.core.multiblock;

import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import forestry.core.config.Constants;

/**
 * In your mod, subscribe this on both the client and server sides side to handle chunk
 * load events for your multiblock machines.
 * Chunks can load asynchronously in environments like MCPC+, so we cannot
 * process any blocks that are in chunks which are still loading.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class MultiblockEventHandler {
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load loadEvent) {
		IChunk chunk = loadEvent.getChunk();
		IWorld world = loadEvent.getWorld();
		MultiblockRegistry.onChunkLoaded(world, chunk.getPos().getRegionX(), chunk.getPos().getRegionZ());
	}

	// Cleanup, for nice memory usageness
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload unloadWorldEvent) {
		MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.getWorld());
	}
}
