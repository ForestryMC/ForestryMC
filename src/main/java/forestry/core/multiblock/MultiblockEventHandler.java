package forestry.core.multiblock;

import java.util.Set;

import forestry.api.multiblock.IMultiblockController;
import forestry.core.utils.Log;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
		World world = loadEvent.getWorld();
		MultiblockRegistry.onChunkLoaded(world, chunk.xPosition, chunk.zPosition);
	}

	// Cleanup, for nice memory usageness
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldUnload(WorldEvent.Unload unloadWorldEvent) {
		MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.getWorld());
	}
	
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event){
		try{
			World world = Minecraft.getMinecraft().world;
			Set<IMultiblockControllerInternal> controllers = MultiblockRegistry.getControllersFromWorld(world);
			if(!controllers.isEmpty()){
		        float partialTicks = event.getPartialTicks();
				EntityPlayer player = Minecraft.getMinecraft().player;
		        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		        
		        GlStateManager.pushMatrix();
		        GlStateManager.enableBlend();
		        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		        GlStateManager.disableTexture2D();
		        GlStateManager.glLineWidth(2.0F);
	            GlStateManager.depthMask(false);
		        for(IMultiblockController controller : controllers){
					if(controller != null){
						BlockPos lastErrorPosition = controller.getLastValidationErrorPosition();
						if(lastErrorPosition != null){
							if(world.isBlockLoaded(lastErrorPosition) && player.getDistanceSq(lastErrorPosition) < 64F){
								AxisAlignedBB box = Block.FULL_BLOCK_AABB.offset(lastErrorPosition.getX() - playerX, lastErrorPosition.getY() - playerY, lastErrorPosition.getZ() - playerZ);
								RenderGlobal.drawSelectionBoundingBox(box, 1.0F, 0.0F, 0.0F, 0.25F);
								RenderGlobal.renderFilledBox(box, 1.0F, 0.0F, 0.0F, 0.125F);
							}
						}
					}
				}
				
	            GlStateManager.depthMask(true);
		        GlStateManager.enableTexture2D();
		        GlStateManager.disableBlend();
		        GlStateManager.popMatrix();
			}
		}catch(Exception e){
			Log.error("Failed to render the position of a multiblock exeption.", e);
		}
	}
}
