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
package forestry.core.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.api.core.IToolPipette;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.fluids.StandardTank;
import forestry.core.gui.IContainerLiquidTanks;
import forestry.core.utils.ResourceUtil;
import forestry.farming.gui.ContainerFarm;

/**
 * Slot for liquid tanks
 */
@OnlyIn(Dist.CLIENT)
public class TankWidget extends Widget {

	private int overlayTexX = 176;
	private int overlayTexY = 0;
	private int slot;
	protected boolean drawOverlay = true;

	public TankWidget(WidgetManager manager, int xPos, int yPos, int slot) {
		super(manager, xPos, yPos);
		this.slot = slot;
		this.height = 58;
	}

	public TankWidget setOverlayOrigin(int x, int y) {
		overlayTexX = x;
		overlayTexY = y;
		return this;
	}

	@Nullable
	public IFluidTank getTank() {
		AbstractContainerMenu container = manager.gui.getMenu();
		if (container instanceof IContainerLiquidTanks) {
			return ((IContainerLiquidTanks) container).getTank(slot);
		} else if (container instanceof ContainerFarm) {
			return ((ContainerFarm) container).getTank(slot);
		}
		return null;
	}

	@Override
	public void draw(PoseStack transform, int startY, int startX) {
		RenderSystem.disableBlend();
		IFluidTank tank = getTank();

		if (tank == null || tank.getCapacity() <= 0) {
			return;
		}

		FluidStack contents = tank.getFluid();

		if (!contents.isEmpty() && contents.getAmount() > 0 && contents.getFluid() != null) {
			Fluid fluid = contents.getFluid();

			if (fluid != null) {
				ResourceLocation fluidStill = fluid.getFluidType().getStillTexture(contents);
				TextureAtlasSprite fluidStillSprite = null;
				if (fluidStill != null) {
					fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
				}
				if (fluidStillSprite == null) {
					fluidStillSprite = ResourceUtil.getMissingTexture();
				}

				int fluidColor = fluid.getFluidType().getColor(contents);

				int scaledAmount = contents.getAmount() * height / tank.getCapacity();
				if (contents.getAmount() > 0 && scaledAmount < 1) {
					scaledAmount = 1;
				}
				if (scaledAmount > height) {
					scaledAmount = height;
				}

				RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
				setGLColorFromInt(fluidColor);

				final int xTileCount = width / 16;
				final int xRemainder = width - xTileCount * 16;
				final int yTileCount = scaledAmount / 16;
				final int yRemainder = scaledAmount - yTileCount * 16;

				final int yStart = startY + height;

				for (int xTile = 0; xTile <= xTileCount; xTile++) {
					for (int yTile = 0; yTile <= yTileCount; yTile++) {
						int width = xTile == xTileCount ? xRemainder : 16;
						int height = yTile == yTileCount ? yRemainder : 16;
						int x = startX + xTile * 16;
						int y = yStart - (yTile + 1) * 16;
						if (width > 0 && height > 0) {
							int maskTop = 16 - height;
							int maskRight = 16 - width;

							drawFluidTexture(transform.last().pose(), x + xPos, y + yPos, fluidStillSprite, maskTop, maskRight, 100);
						}
					}
				}
			}
		}

		if (drawOverlay) {
			// RenderSystem.enableAlphaTest();
			RenderSystem.disableDepthTest();
			RenderSystem.setShaderTexture(0, manager.gui.textureFile);
			manager.gui.blit(transform, startX + xPos, startY + yPos, overlayTexX, overlayTexY, 16, 60);
			RenderSystem.enableDepthTest();
			// RenderSystem.disableAlphaTest();
		}

		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		IFluidTank tank = getTank();
		if (!(tank instanceof StandardTank standardTank)) {
			return null;
		}
		return standardTank.getToolTip();
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		RenderSystem.setShaderColor(red, green, blue, 1.0F);
	}

	private static void drawFluidTexture(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
		float uMin = textureSprite.getU0();
		float uMax = textureSprite.getU1();
		float vMin = textureSprite.getV0();
		float vMax = textureSprite.getV1();
		uMax = uMax - maskRight / 16.0F * (uMax - uMin);
		vMax = vMax - maskTop / 16.0F * (vMax - vMin);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
		buffer.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
		buffer.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
		buffer.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
		tessellator.end();
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		Player player = manager.minecraft.player;
		ItemStack itemstack = player.inventoryMenu.getCarried();
		if (itemstack.isEmpty()) {
			return;
		}

		Item held = itemstack.getItem();
		AbstractContainerMenu container = manager.gui.getMenu();
		if (held instanceof IToolPipette && container instanceof IContainerLiquidTanks) {
			((IContainerLiquidTanks) container).handlePipetteClickClient(slot, player);
		}
	}
}
