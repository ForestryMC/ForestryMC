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
package forestry.greenhouse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.multiblock.MultiblockUtil;
import forestry.greenhouse.api.greenhouse.IGreenhouseLimits;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.api.greenhouse.Position2D;
import forestry.greenhouse.items.ItemGreenhouseScreen;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

@SideOnly(Side.CLIENT)
public class GreenhouseEventHandler {

	protected long previousCheckTick = 0;
	protected Set<BlockPos> greenhousePositions = new HashSet<>();

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		try {
			float partialTicks = event.getPartialTicks();
			EntityPlayer player = Minecraft.getMinecraft().player;
			World world = player.world;
			long tick = world.getTotalWorldTime();
			if (tick > previousCheckTick + 50) {
				greenhousePositions.clear();
				for (ItemStack itemStack : getClimateScreen(player.inventory)) {
					if (!itemStack.isEmpty()) {
						BlockPos position = ItemGreenhouseScreen.getGreenhousePos(itemStack);
						IGreenhouseControllerInternal controller = MultiblockUtil.getController(world, position, IGreenhouseComponent.class);
						if (controller == null || !controller.isAssembled()) {
							return;
						}
						position = controller.getCenterCoordinates();
						double distance = MathHelper.sqrt(player.getDistanceSqToCenter(position));
						if (distance > 64F) {
							return;
						}
						greenhousePositions.add(position);
					}
				}
				previousCheckTick = tick;
			}
			for (BlockPos position : greenhousePositions) {
				IGreenhouseControllerInternal controller = MultiblockUtil.getController(world, position, IGreenhouseComponent.class);
				if(controller == null || !controller.isAssembled()){
					continue;
				}
				IGreenhouseProvider provider = controller.getProvider();
				position = controller.getCenterCoordinates();
				double distance = MathHelper.sqrt(player.getDistanceSqToCenter(position));
				if (distance > 64F) {
					return;
				}
				double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
				double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
				double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
				GlStateManager.pushMatrix();
				GlStateManager.translate(-playerX, -playerY, -playerZ);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.disableTexture2D();
				GlStateManager.glLineWidth(2.5F);
				//Draw center position
				BlockPos offset = provider.getCenterPos();
				AxisAlignedBB testBlockBB = Block.FULL_BLOCK_AABB.offset(offset);
				RenderGlobal.drawSelectionBoundingBox(testBlockBB, 0.0F, 0.0F, 0.0F, 0.5F);
				IGreenhouseLimits limits = provider.getUsedLimits();
				if (limits != null) {
					Position2D minEdge = limits.getMinimumCoordinates();
					Position2D maxEdge = limits.getMaximumCoordinates();
					AxisAlignedBB greenhouseBB = new AxisAlignedBB(minEdge.getX(), limits.getDepth(), minEdge.getZ(), maxEdge.getX() + 1, limits.getHeight() + 1, maxEdge.getZ() + 1);
					RenderGlobal.drawSelectionBoundingBox(greenhouseBB, 0.0F, 0.0F, 0.0F, 0.5F);
				}
				GlStateManager.enableTexture2D();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		previousCheckTick = 0;
	}

	private NonNullList<ItemStack> getClimateScreen(InventoryPlayer inventoryPlayer) {
		NonNullList<ItemStack> climateScreens = NonNullList.create();
		List<ItemStack> items = NonNullList.create();
		items.addAll(inventoryPlayer.mainInventory);
		items.addAll(inventoryPlayer.offHandInventory);
		for (ItemStack stack : items) {
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() != ModuleGreenhouse.getItems().greenhouseScreen) {
				continue;
			}
			if (ItemGreenhouseScreen.isPreviewModeActive(stack) && ItemGreenhouseScreen.isValid(stack, inventoryPlayer.player.world)) {
				climateScreens.add(stack);
			}
		}
		return climateScreens;
	}

}
