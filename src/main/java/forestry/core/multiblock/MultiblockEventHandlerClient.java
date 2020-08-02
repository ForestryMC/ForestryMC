package forestry.core.multiblock;

import java.util.Set;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Log;

@OnlyIn(Dist.CLIENT)
public class MultiblockEventHandlerClient {

    //TODO - register event handler
    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent.Post event) {
        if (GeneticsUtil.hasNaturalistEye(Minecraft.getInstance().player)) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && Minecraft.getInstance().currentScreen == null) {
                Minecraft minecraft = Minecraft.getInstance();
                RayTraceResult posHit = minecraft.objectMouseOver;
                MainWindow window = event.getWindow();

                if (posHit != null && posHit.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult brayTrace = (BlockRayTraceResult) posHit;
                    TileUtil.actOnTile(minecraft.world, ((BlockRayTraceResult) posHit).getPos(), IMultiblockComponent.class, component -> {
                        IMultiblockController controller = component.getMultiblockLogic().getController();
                        //TODO - make this a textcomponent
                        String lastValidationError = controller.getLastValidationError();
                        if (lastValidationError != null) {
                            lastValidationError = TextFormatting.DARK_RED.toString() + TextFormatting.ITALIC.toString() + lastValidationError;
                            //minecraft.fontRenderer.drawSplitString(lastValidationError, window.getScaledWidth() / 2 + 35, window.getScaledHeight() / 2 - 25, 128, 16777215);
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        if (GeneticsUtil.hasNaturalistEye(Minecraft.getInstance().player)) {
            try {
                World world = Minecraft.getInstance().world;
                Set<IMultiblockControllerInternal> controllers = MultiblockRegistry.getControllersFromWorld(world);
                if (!controllers.isEmpty()) {
                    float partialTicks = event.getPartialTicks();
                    PlayerEntity player = Minecraft.getInstance().player;
                    double playerX = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * partialTicks;
                    double playerY = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * partialTicks;
                    double playerZ = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * partialTicks;

                    RenderSystem.pushMatrix();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    RenderSystem.disableTexture();
                    RenderSystem.lineWidth(2.0F);
                    RenderSystem.depthMask(false);
                    for (IMultiblockController controller : controllers) {
                        if (controller != null) {
                            BlockPos lastErrorPosition = controller.getLastValidationErrorPosition();
                            if (lastErrorPosition != null) {
                                if (world.isBlockLoaded(lastErrorPosition) && player.getDistanceSq(lastErrorPosition.getX(), lastErrorPosition.getZ(), lastErrorPosition.getZ()) < 64F) {
                                    AxisAlignedBB box = VoxelShapes.fullCube().getBoundingBox().offset(lastErrorPosition.getX() - playerX, lastErrorPosition.getY() - playerY, lastErrorPosition.getZ() - playerZ);
                                    //WorldRenderer.drawSelectionBoundingBox(box, 1.0F, 0.0F, 0.0F, 0.25F);
                                    //WorldRenderer.drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1.0F, 0.0F, 0.0F, 0.125F);    //TODO right method?
                                    //TODO: Rendering
                                }
                            }
                        }
                    }

                    RenderSystem.depthMask(true);
                    RenderSystem.enableTexture();
                    RenderSystem.disableBlend();
                    RenderSystem.popMatrix();
                }
            } catch (Exception e) {
                Log.error("Failed to render the position of a multiblock exception.", e);
            }
        }
    }
}
