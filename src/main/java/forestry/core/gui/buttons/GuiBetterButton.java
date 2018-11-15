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
package forestry.core.gui.buttons;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Constants;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.render.ForestryResource;

@SideOnly(Side.CLIENT)
public class GuiBetterButton extends GuiButton implements IToolTipProvider {

	public static final ResourceLocation TEXTURE = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/buttons.png");
	protected IButtonTextureSet texture;
	@Nullable
	private ToolTip toolTip;
	private boolean useTexWidth = false;

	public GuiBetterButton(int id, int x, int y, IButtonTextureSet texture) {
		super(id, x, y, texture.getWidth(), texture.getHeight(), "");
		this.texture = texture;
		useTexWidth = true;
	}

	public GuiBetterButton setTexture(IButtonTextureSet texture) {
		this.texture = texture;
		width = texture.getWidth();
		height = texture.getHeight();
		return this;
	}

	public GuiBetterButton setUseTextureWidth() {
		useTexWidth = true;
		return this;
	}

	public GuiBetterButton setGuiWidth(int width) {
		this.width = width;
		useTexWidth = false;
		return this;
	}

	public GuiBetterButton setLabel(String label) {
		this.displayString = label;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return texture.getHeight();
	}

	public int getTextColor(boolean mouseOver) {
		if (!enabled) {
			return 0xffa0a0a0;
		} else if (mouseOver) {
			return 0xffffa0;
		} else {
			return 0xe0e0e0;
		}
	}

	public boolean isMouseOverButton(int mouseX, int mouseY) {
		return mouseX >= x && mouseY >= y && mouseX < x + getWidth() && mouseY < y + getHeight();
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
		if (!visible) {
			return;
		}
		FontRenderer fontrenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int xOffset = texture.getX();
		int yOffset = texture.getY();
		int h = texture.getHeight();
		int w = texture.getWidth();
		boolean mouseOver = isMouseOverButton(mouseX, mouseY);
		int hoverState = getHoverState(mouseOver);
		if (useTexWidth) {
			drawTexturedModalRect(x, y, xOffset, yOffset + hoverState * h, w, h);
		} else {
			drawTexturedModalRect(x, y, xOffset, yOffset + hoverState * h, width / 2, h);
			drawTexturedModalRect(x + width / 2, y, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
		}
		mouseDragged(minecraft, mouseX, mouseY);
		drawCenteredString(fontrenderer, displayString, x + getWidth() / 2, y + (h - 8) / 2, getTextColor(mouseOver));
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return toolTip;
	}

	public void setToolTip(ToolTip tips) {
		this.toolTip = tips;
	}

	@Override
	public boolean isToolTipVisible() {
		return visible;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return isMouseOverButton(mouseX, mouseY);
	}
}
