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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.IClimateTransformer;
import forestry.climatology.items.ItemHabitatScreen;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.TickHelper;

@SideOnly(Side.CLIENT)
public class PreviewHandlerClient {

	public static final PreviewHandlerClient INSTANCE = new PreviewHandlerClient();

	private static final AxisAlignedBB BOUNDING_BOX = Block.FULL_BLOCK_AABB.shrink(0.125F);

	private TickHelper tickHelper = new TickHelper();
	private final Set<BlockPos> previewPositions = new HashSet<>();
	@Nullable
	private BlockPos previewOrigin = null;
	private int range = -1;
	private boolean circular;


	private PreviewHandlerClient() {
	}

	public void setPreview(BlockPos centerPosition, int range, boolean circular) {
		previewPositions.clear();
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
					previewPositions.add(position);
				}
			}
		}
		previewOrigin = centerPosition;
		this.range = range;
		this.circular = circular;
	}

	public boolean isPreviewOrigin(BlockPos pos){
		return previewOrigin != null && pos.equals(previewOrigin);
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
	}

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		if(previewPositions.isEmpty()){
			return;
		}
		float partialTicks = event.getPartialTicks();
		EntityPlayer player = Minecraft.getMinecraft().player;
		double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(-playerX, -playerY, -playerZ);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(6.0F);
		GlStateManager.disableTexture2D();

		for (BlockPos position : previewPositions) {
			AxisAlignedBB boundingBox = BOUNDING_BOX.offset(position);
			RenderGlobal.renderFilledBox(boundingBox, 0.75F, 0.5F, 0.0F, 0.45F);
		}
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent tickEvent) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player == null){
			return;
		}
		World world = player.world;
		tickHelper.onTick();
		if(tickHelper.updateOnInterval(100)){
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			if(stack.isEmpty() || stack.getItem() != ModuleClimatology.getItems().habitatScreen || !ItemHabitatScreen.isValid(stack, player.world)){
				clearPreview();
				return;
			}
			BlockPos currentPos = ItemHabitatScreen.getPosition(stack);
			if (currentPos == null || player.getDistance(currentPos.getX(), currentPos.getY(), currentPos.getZ()) > 128F) {
				clearPreview();
				return;
			}
			IClimateTransformer transformer = TileUtil.getInterface(world, currentPos, ClimateCapabilities.CLIMATE_TRANSFORMER, null);
			if(transformer == null){
				clearPreview();
				return;
			}
			updatePreview(currentPos, transformer.getRange(), transformer.isCircular());
		}
	}

	@SubscribeEvent
	public void worldUnloaded(WorldEvent.Unload event) {
		clearPreview();
	}
}
