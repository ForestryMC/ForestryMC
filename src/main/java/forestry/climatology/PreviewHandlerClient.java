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
package forestry.climatology;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.Level;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.IClimateTransformer;
import forestry.climatology.features.ClimatologyItems;
import forestry.climatology.items.ItemHabitatScreen;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.TickHelper;

@OnlyIn(Dist.CLIENT)
public class PreviewHandlerClient {

	private final TickHelper tickHelper = new TickHelper();
	private final PreviewRenderer renderer = new PreviewRenderer();

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent tickEvent) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		Level world = player.level;
		tickHelper.onTick();
		if (tickHelper.updateOnInterval(100)) {
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (stack.isEmpty()
					|| !ClimatologyItems.HABITAT_SCREEN.itemEqual(stack)
					|| !ItemHabitatScreen.isValid(stack, player.level)
					|| !ItemHabitatScreen.isPreviewModeActive(stack)) {
				renderer.clearPreview();
				return;
			}
			BlockPos currentPos = ItemHabitatScreen.getPosition(stack);
			if (currentPos == null
					|| player.distanceToSqr(currentPos.getX(), currentPos.getY(), currentPos.getZ()) > 128 * 128F) {
				renderer.clearPreview();
				return;
			}
			LazyOptional<IClimateTransformer> transformer = TileUtil.getInterface(world, currentPos, ClimateCapabilities.CLIMATE_TRANSFORMER, null);
			if (!transformer.isPresent()) {
				renderer.clearPreview();
				return;
			}
			transformer.ifPresent(t ->
				renderer.updatePreview(currentPos, t.getRange(), t.isCircular())
			);
		}
	}

	@SubscribeEvent
	public void worldUnloaded(WorldEvent.Unload event) {
		renderer.clearPreview();
	}

	private class PreviewRenderer {
		private final Set<BlockPos> previewPositions = new HashSet<>();
		private final AABB boundingBox = Shapes.block().bounds().deflate(0.125F);
		private boolean addedToBus = false;
		@Nullable
		private BlockPos previewOrigin = null;
		private int range = -1;
		private boolean circular;

		public void setPreview(BlockPos centerPosition, int range, boolean circular) {
			previewPositions.clear();
			BlockPos center = new BlockPos(0, 0, 0);
			for (int x = -range; x <= range; x++) {
				for (int y = -range; y <= range; y++) {
					BlockPos position = centerPosition.offset(x, 0, y);
					boolean valid;
					if (circular) {
						double distance = Math.round(Math.sqrt(center.distSqr(x, 0, y, true)));
						valid = distance <= range && distance > (range - 1);
					} else {
						valid = !(!(x == -range || x == range) && !(y == -range || y == range));
					}
					if (valid) {
						previewPositions.add(position);
					}
				}
			}
			previewOrigin = centerPosition;
			this.range = range;
			this.circular = circular;
			if (!addedToBus) {
				addedToBus = true;
				MinecraftForge.EVENT_BUS.register(this);
			}
		}

		public boolean isPreviewOrigin(BlockPos pos) {
			return pos.equals(previewOrigin);
		}

		@Nullable
		public BlockPos getPreviewOrigin() {
			return previewOrigin;
		}

		public void updatePreview(BlockPos centerPosition, int range, boolean circular) {
			if (centerPosition == previewOrigin || range != this.range || circular != this.circular) {
				setPreview(centerPosition, range, circular);
			}
		}

		public void clearPreview() {
			previewOrigin = null;
			circular = false;
			range = -1;
			previewPositions.clear();
			MinecraftForge.EVENT_BUS.unregister(renderer);
			addedToBus = false;
		}

		@SubscribeEvent
		public void onWorldRenderLast(RenderWorldLastEvent event) {
			if (previewPositions.isEmpty()) {
				return;
			}
			float partialTicks = event.getPartialTicks();
			Player player = Minecraft.getInstance().player;
			double playerX = player.xOld + (player.getX() - player.xOld) * partialTicks;
			double playerY = player.yOld + (player.getY() - player.yOld) * partialTicks;
			double playerZ = player.zOld + (player.getZ() - player.zOld) * partialTicks;
			RenderSystem.pushMatrix();
			RenderSystem.translated(-playerX, -playerY, -playerZ);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.lineWidth(6.0F);
			RenderSystem.disableDepthTest();

			for (BlockPos position : previewPositions) {
				AABB boundingBox = this.boundingBox.move(position);
				//				WorldRenderer.renderFilledBox(boundingBox, 0.75F, 0.5F, 0.0F, 0.45F);
				//TODO rendering
			}
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.popMatrix();

		}
	}
}
