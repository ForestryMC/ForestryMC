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
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Constants;
import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.render.ForestryResource;

@OnlyIn(Dist.CLIENT)
public class GuiBetterButton extends Button implements IToolTipProvider {

	public static final ResourceLocation TEXTURE = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/buttons.png");
	protected IButtonTextureSet texture;
	@Nullable
	private ToolTip toolTip;
	private boolean useTexWidth = false;

	public GuiBetterButton(int x, int y, IButtonTextureSet texture) {
		this(x, y, texture, b -> {
		});
	}

	public GuiBetterButton(int x, int y, IButtonTextureSet texture, IPressable handler) {
		super(x, y, texture.getWidth(), texture.getHeight(), "", handler);
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
		this.setMessage(label);    //TODO check method call
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return texture.getHeight();
	}

	public int getTextColor(boolean mouseOver) {
		if (!visible) {    //TODO right bool?
			return 0xffa0a0a0;
		} else if (mouseOver) {
			return 0xffffa0;
		} else {
			return 0xe0e0e0;
		}
	}

	public boolean isMouseOverButton(double mouseX, double mouseY) {
		return mouseX >= x && mouseY >= y && mouseX < x + getWidth() && mouseY < y + getHeight();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (!visible) {
			return;
		}
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int xOffset = texture.getX();
		int yOffset = texture.getY();
		int h = texture.getHeight();
		int w = texture.getWidth();
		boolean mouseOver = isMouseOverButton(mouseX, mouseY);
		int hoverState = getYImage(isHovered());    //TODO this is what botania uses for hoverstate
		if (useTexWidth) {
			blit(x, y, xOffset, yOffset + hoverState * h, w, h);
		} else {
			blit(x, y, xOffset, yOffset + hoverState * h, width / 2, h);
			blit(x + width / 2, y, xOffset + w - width / 2, yOffset + hoverState * h, width / 2, h);
		}

		//TODO mousedragged
		//		mouseDragged(minecraft, mouseX, mouseY);
		drawCenteredString(Minecraft.getInstance().fontRenderer, getMessage(), x + getWidth() / 2, y + (h - 8) / 2, getTextColor(mouseOver));
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
	public boolean isMouseOver(double mouseX, double mouseY) {
		return isMouseOverButton(mouseX, mouseY);
	}
}
