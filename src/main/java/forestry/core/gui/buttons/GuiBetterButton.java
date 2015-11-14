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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Constants;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

@SideOnly(Side.CLIENT)
public class GuiBetterButton extends GuiButton implements IToolTipProvider {

	private static final ResourceLocation TEXTURE = new ResourceLocation("forestry", Constants.TEXTURE_PATH_GUI + "/buttons.png");
	protected IButtonTextureSet texture;
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

	public GuiBetterButton setWidth(int width) {
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
		return mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + getWidth() && mouseY < yPosition + getHeight();
	}

	protected static void bindButtonTextures(Minecraft minecraft) {
		minecraft.renderEngine.bindTexture(TEXTURE);
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (!visible) {
			return;
		}
		FontRenderer fontrenderer = minecraft.fontRenderer;
		bindButtonTextures(minecraft);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int xOffset = texture.getX();
		int yOffset = texture.getY();
		int h = texture.getHeight();
		int w = texture.getWidth();
		boolean mouseOver = isMouseOverButton(mouseX, mouseY);
		int hoverState = getHoverState(mouseOver);
		if (useTexWidth) {
			drawTexturedModalRect(xPosition, yPosition, xOffset, yOffset + hoverState * h, w, h);
		} else {
			drawTexturedModalRect(xPosition, yPosition, xOffset, yOffset + hoverState * h, width / 2, h);
			drawTexturedModalRect(xPosition + width / 2, yPosition, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
		}
		mouseDragged(minecraft, mouseX, mouseY);
		drawCenteredString(fontrenderer, displayString, xPosition + getWidth() / 2, yPosition + (h - 8) / 2, getTextColor(mouseOver));
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
