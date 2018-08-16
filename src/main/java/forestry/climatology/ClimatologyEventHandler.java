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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClimatologyEventHandler {
	private static final Set<BlockPos> DEBUG_POSITIONS = new HashSet<>();
	@Nullable
	private static BlockPos currentFormer = null;

	@Nullable
	public static BlockPos getCurrentFormer() {
		return currentFormer;
	}

	public static void setDebugPositions(BlockPos centerPosition, int range, boolean circular) {
		BlockPos center = new BlockPos(0, 0, 0);
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				BlockPos position = centerPosition.add(x, 0, y);
				boolean valid;
				if (circular) {
					double distance = Math.round(center.getDistance(x, 0, y));
					valid = distance <= range && distance > (range - 1);
				} else {
					valid = !(!(x == -range || x == range) && !(y == -range || y == range));
				}
				if (valid) {
					DEBUG_POSITIONS.add(position);
				}
			}
		}
		currentFormer = centerPosition;
	}

	public static void updateDebugPositions(BlockPos centerPosition, int range, boolean circular) {
		if (centerPosition == currentFormer) {
			setDebugPositions(centerPosition, range, circular);
		}
	}

	public static void clearDebugPositions() {
		currentFormer = null;
		DEBUG_POSITIONS.clear();
	}

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		float partialTicks = event.getPartialTicks();
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (currentFormer == null || player.getDistance(currentFormer.getX(), currentFormer.getY(), currentFormer.getZ()) > 128F) {
			return;
		}
		double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(-playerX, -playerY, -playerZ);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(6.0F);
		GlStateManager.disableTexture2D();

		for (BlockPos position : DEBUG_POSITIONS) {
			AxisAlignedBB boundingBox = Block.FULL_BLOCK_AABB.shrink(0.125F).offset(position);
			RenderGlobal.renderFilledBox(boundingBox, 0.75F, 0.5F, 0.0F, 0.45F);
		}
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		if (getCurrentFormer() == event.getPos()) {
			clearDebugPositions();
		}
	}

	@SubscribeEvent
	public void worldUnloaded(WorldEvent.Unload event) {
		clearDebugPositions();
	}
}
