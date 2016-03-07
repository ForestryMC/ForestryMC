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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import forestry.api.core.IToolPipette;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gui.IContainerLiquidTanks;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.proxy.Proxies;
import forestry.farming.gui.ContainerFarm;

/**
 * Slot for liquid tanks
 */
public class TankWidget extends Widget {

	private int overlayTexX = 176;
	private int overlayTexY = 0;
	private int slot = 0;
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

	public IFluidTank getTank() {
		Container container = manager.gui.inventorySlots;
		if (container instanceof IContainerLiquidTanks) {
			return ((IContainerLiquidTanks) container).getTank(slot);
		} else if (container instanceof ContainerFarm) {
			return ((ContainerFarm) container).getTank(slot);
		}
		return null;
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.disableBlend();
		IFluidTank tank = getTank();
		if (tank == null || tank.getCapacity() <= 0) {
			return;
		}

		FluidStack contents = tank.getFluid();
		if (contents == null || contents.amount <= 0 || contents.getFluid() == null) {
			return;
		}
		
		Fluid fluid = contents.getFluid();
		if (fluid == null) {
			return;
		}

		TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
		ResourceLocation fluidStill = fluid.getStill();
		TextureAtlasSprite fluidStillSprite = null;
		if (fluidStill != null) {
			fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
		}
		if (fluidStillSprite == null) {
			fluidStillSprite = textureMapBlocks.getMissingSprite();
		}

		int fluidColor = fluid.getColor(contents);

		int scaledAmount = (contents.amount * height) / tank.getCapacity();
		if (contents.amount > 0 && scaledAmount < 1) {
			scaledAmount = 1;
		}
		if (scaledAmount > height) {
			scaledAmount = height;
		}

		Proxies.render.bindTexture(TextureMap.locationBlocksTexture);
		setGLColorFromInt(fluidColor);

		final int xTileCount = width / 16;
		final int xRemainder = width - (xTileCount * 16);
		final int yTileCount = scaledAmount / 16;
		final int yRemainder = scaledAmount - (yTileCount * 16);

		final int yStart = startY + height;

		for (int xTile = 0; xTile <= xTileCount; xTile++) {
			for (int yTile = 0; yTile <= yTileCount; yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : 16;
				int height = (yTile == yTileCount) ? yRemainder : 16;
				int x = startX + (xTile * 16);
				int y = yStart - ((yTile + 1) * 16);
				if (width > 0 && height > 0) {
					int maskTop = 16 - height;
					int maskRight = 16 - width;

					drawFluidTexture(x + xPos, y + yPos, fluidStillSprite, maskTop, maskRight, 100);
				}
			}
		}
		
		if (drawOverlay) {
			Proxies.render.bindTexture(manager.gui.textureFile);
			manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, overlayTexX, overlayTexY, 16, 60);
		}
		
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		IFluidTank tank = getTank();
		if (!(tank instanceof StandardTank)) {
			return null;
		}
		return ((StandardTank) tank).getToolTip();
	}
	
	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		GlStateManager.color(red, green, blue, 1.0F);
	}

	
	private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		double uMin = (double) textureSprite.getMinU();
		double uMax = (double) textureSprite.getMaxU();
		double vMin = (double) textureSprite.getMinV();
		double vMax = (double) textureSprite.getMaxV();
		uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
		vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(xCoord, yCoord + 16, zLevel).tex(uMin,  vMax).endVertex();
		worldrenderer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
		worldrenderer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
		worldrenderer.pos(xCoord, yCoord + maskTop,  zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		EntityPlayer player = manager.minecraft.thePlayer;
		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null) {
			return;
		}

		Item held = itemstack.getItem();
		Container container = manager.gui.inventorySlots;
		if (held instanceof IToolPipette && container instanceof IContainerLiquidTanks) {
			((IContainerLiquidTanks) container).handlePipetteClickClient(slot, player);
		}
	}
}
