package forestry.core.multiblock;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Log;

@SideOnly(Side.CLIENT)
public class MultiblockEventHandlerClient {

	@SubscribeEvent
	public void onGameOverlay(RenderGameOverlayEvent.Post event) {
		if (GeneticsUtil.hasNaturalistEye(Minecraft.getMinecraft().player)) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && Minecraft.getMinecraft().currentScreen == null) {
				Minecraft minecraft = Minecraft.getMinecraft();
				RayTraceResult posHit = minecraft.objectMouseOver;
				ScaledResolution resolution = event.getResolution();

				if (posHit != null && posHit.getBlockPos() != null) {
					TileUtil.actOnTile(minecraft.world, posHit.getBlockPos(), IMultiblockComponent.class, component -> {
						IMultiblockController controller = component.getMultiblockLogic().getController();
						String lastValidationError = controller.getLastValidationError();
						if (lastValidationError != null) {
							lastValidationError = TextFormatting.DARK_RED.toString() + TextFormatting.ITALIC.toString() + lastValidationError;
							minecraft.fontRenderer.drawSplitString(lastValidationError, resolution.getScaledWidth() / 2 + 35, resolution.getScaledHeight() / 2 - 25, 128, 16777215);
						}
					});
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		if (GeneticsUtil.hasNaturalistEye(Minecraft.getMinecraft().player)) {
			try {
				World world = Minecraft.getMinecraft().world;
				Set<IMultiblockControllerInternal> controllers = MultiblockRegistry.getControllersFromWorld(world);
				if (!controllers.isEmpty()) {
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
					for (IMultiblockController controller : controllers) {
						if (controller != null) {
							BlockPos lastErrorPosition = controller.getLastValidationErrorPosition();
							if (lastErrorPosition != null) {
								if (world.isBlockLoaded(lastErrorPosition) && player.getDistanceSq(lastErrorPosition) < 64F) {
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
			} catch (Exception e) {
				Log.error("Failed to render the position of a multiblock exception.", e);
			}
		}
	}
}
