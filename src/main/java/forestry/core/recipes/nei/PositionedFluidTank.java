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
package forestry.core.recipes.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;

public class PositionedFluidTank {

	public FluidTank[] tanks;
	public FluidTank tank;
	public Rectangle position;
	public String overlayTexture;
	public Point overlayTexturePos;
	public boolean flowingTexture = false;
	public boolean showAmount = true;
	public boolean perTick = false;

	public PositionedFluidTank(FluidTank[] tanks, Rectangle position, String overlayTexture, Point overlayTexturePos) {
		this.position = position;
		this.tanks = tanks;
		this.tank = tanks[0];
		this.overlayTexture = overlayTexture;
		this.overlayTexturePos = overlayTexturePos;
	}

	public PositionedFluidTank(Collection<FluidStack> fluids, int capacity, Rectangle position, String overlayTexture, Point overlayTexturePos) {
		this(createFluidTanks(capacity, fluids), position, overlayTexture, overlayTexturePos);
	}

	public PositionedFluidTank(FluidStack fluid, int capacity, Rectangle position, String overlayTexture, Point overlayTexturePos) {
		this(createFluidTanks(capacity, Collections.singletonList(fluid)), position, overlayTexture, overlayTexturePos);
	}

	public PositionedFluidTank(FluidStack fluid, int capacity, Rectangle position) {
		this(fluid, capacity, position, null, null);
	}

	private static FluidTank[] createFluidTanks(int capacity, Collection<FluidStack> fluidStacks) {
		FluidTank[] tanks = new FluidTank[fluidStacks.size()];
		int i = 0;
		for (FluidStack fluidStack : fluidStacks) {
			tanks[i++] = new FluidTank(fluidStacks != null ? fluidStack.copy() : null, capacity);
		}
		return tanks;
	}

	public List<String> handleTooltip(List<String> currenttip) {
		if (this.tank == null || this.tank.getFluid() == null || this.tank.getFluid().getFluid() == null || this.tank.getFluid().amount <= 0) {
			return currenttip;
		}
		currenttip.add(this.tank.getFluid().getLocalizedName());
		if (this.showAmount) {
			currenttip.add(EnumChatFormatting.GRAY.toString() + this.tank.getFluid().amount + (this.perTick ? " mB/t" : " mB"));
		}
		return currenttip;
	}

	public boolean transfer(boolean usage) {
		if (this.tank.getFluid() != null && this.tank.getFluid().amount > 0) {
			if (usage) {
				if (!GuiUsageRecipe.openRecipeGui("liquid", this.tank.getFluid())) {
					return false;
				}
			} else {
				if (!GuiCraftingRecipe.openRecipeGui("liquid", this.tank.getFluid())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public void draw() {
		if (this.tank == null || this.tank.getFluid() == null || this.tank.getFluid().getFluid() == null || this.tank.getFluid().amount <= 0) {
			return;
		}
		final IIcon fluidIcon;
		if (this.flowingTexture && this.tank.getFluid().getFluid().getFlowingIcon() != null) {
			fluidIcon = this.tank.getFluid().getFluid().getFlowingIcon();
		} else if (this.tank.getFluid().getFluid().getStillIcon() != null) {
			fluidIcon = this.tank.getFluid().getFluid().getStillIcon();
		} else {
			return;
		}

		GuiDraw.changeTexture(TextureMap.locationBlocksTexture);
		int color = this.tank.getFluid().getFluid().getColor(this.tank.getFluid());
		GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

		int amount = Math.max(Math.min(this.position.height, this.tank.getFluid().amount * this.position.height / this.tank.getCapacity()), 1);
		int posY = this.position.y + this.position.height - amount;

		for (int i = 0; i < this.position.width; i += 16) {
			for (int j = 0; j < amount; j += 16) {
				int drawWidth = Math.min(this.position.width - i, 16);
				int drawHeight = Math.min(amount - j, 16);

				int drawX = this.position.x + i;
				int drawY = posY + j;

				double minU = fluidIcon.getMinU();
				double maxU = fluidIcon.getMaxU();
				double minV = fluidIcon.getMinV();
				double maxV = fluidIcon.getMaxV();

				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(drawX, drawY + drawHeight, 0, minU, minV + (maxV - minV) * drawHeight / 16F);
				tessellator.addVertexWithUV(drawX + drawWidth, drawY + drawHeight, 0, minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F);
				tessellator.addVertexWithUV(drawX + drawWidth, drawY, 0, minU + (maxU - minU) * drawWidth / 16F, minV);
				tessellator.addVertexWithUV(drawX, drawY, 0, minU, minV);
				tessellator.draw();
			}
		}

		if (this.overlayTexture != null && this.overlayTexturePos != null) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GuiDraw.changeTexture(this.overlayTexture);
			GuiDraw.drawTexturedModalRect(this.position.x, this.position.y, this.overlayTexturePos.x, this.overlayTexturePos.y, this.position.width, this.position.height);
		}
	}

	public void setPermutationToRender(int index) {
		this.tank = this.tanks[index];
	}

	public int getPermutationCount() {
		return this.tanks.length;
	}
}
