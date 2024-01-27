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
package forestry.core.gui.ledgers;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.SessionVars;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ForestryResource;
import forestry.core.render.TextureManagerForestry;

/**
 * Side ledger for guis
 */
@OnlyIn(Dist.CLIENT)
public abstract class Ledger {

	protected static final int minWidth = 24;
	public static final int minHeight = 24;
	protected final int maxWidth;
	protected final int maxTextWidth;
	protected int maxHeight = 24;

	private static final ResourceLocation ledgerTextureRight = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/ledger.png");
	private static final ResourceLocation ledgerTextureLeft = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/ledger_left.png");

	protected final LedgerManager manager;

	private final int fontColorHeader;
	private final int fontColorText;
	private final int fontColorSubheader;
	private final int overlayColor;

	private boolean open;

	public int currentShiftX = 0;
	public int currentShiftY = 0;

	protected float currentWidth = minWidth;
	protected float currentHeight = minHeight;
	private int x;
	private int y;

	private final ResourceLocation texture;

	protected Ledger(LedgerManager manager, String name) {
		this(manager, name, true);
	}

	protected Ledger(LedgerManager manager, String name, boolean rightSide) {
		this.manager = manager;
		if (rightSide) {
			texture = ledgerTextureRight;
		} else {
			texture = ledgerTextureLeft;
		}

		fontColorHeader = manager.gui.getFontColor().get("ledger." + name + ".header");
		fontColorSubheader = manager.gui.getFontColor().get("ledger." + name + ".subheader");
		fontColorText = manager.gui.getFontColor().get("ledger." + name + ".text");
		overlayColor = manager.gui.getFontColor().get("ledger." + name + ".background");

		maxWidth = Math.min(124, manager.getMaxWidth());
		maxTextWidth = maxWidth - 18;
	}

	public Rect2i getArea() {
		GuiForestry gui = manager.gui;
		return new Rect2i(gui.getGuiLeft() + x, gui.getGuiTop() + y, (int) currentWidth, (int) currentHeight);
	}

	// adjust the update's move amount to match the look of 60 fps (16.67 ms per update)
	private static final float msPerUpdate = 16.667f;
	private long lastUpdateTime = 0;

	public void update() {

		long updateTime;
		if (lastUpdateTime == 0) {
			lastUpdateTime = System.currentTimeMillis();
			updateTime = lastUpdateTime + Math.round(msPerUpdate);
		} else {
			updateTime = System.currentTimeMillis();
		}

		float moveAmount = Config.guiTabSpeed * (updateTime - lastUpdateTime) / msPerUpdate;

		lastUpdateTime = updateTime;

		// Width
		if (open && currentWidth < maxWidth) {
			currentWidth += moveAmount;
			if (currentWidth > maxWidth) {
				currentWidth = maxWidth;
			}
		} else if (!open && currentWidth > minWidth) {
			currentWidth -= moveAmount;
			if (currentWidth < minWidth) {
				currentWidth = minWidth;
			}
		}

		// Height
		if (open && currentHeight < maxHeight) {
			currentHeight += moveAmount;
			if (currentHeight > maxHeight) {
				currentHeight = maxHeight;
			}
		} else if (!open && currentHeight > minHeight) {
			currentHeight -= moveAmount;
			if (currentHeight < minHeight) {
				currentHeight = minHeight;
			}
		}
	}

	public int getHeight() {
		return Math.round(currentHeight);
	}

	public int getWidth() {
		return Math.round(currentWidth);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@OnlyIn(Dist.CLIENT)
	public final void draw(PoseStack transform) {
		draw(transform, y, x);
	}

	@OnlyIn(Dist.CLIENT)
	public abstract void draw(PoseStack transform, int y, int x);

	public abstract Component getTooltip();

	public boolean handleMouseClicked(double x, double y, int mouseButton) {
		return false;
	}

	public boolean intersects(double mouseX, double mouseY) {
		return mouseX >= currentShiftX && mouseX <= currentShiftX + currentWidth && mouseY >= currentShiftY && mouseY <= currentShiftY + getHeight();
	}

	public void setFullyOpen() {
		open = true;
		currentWidth = maxWidth;
		currentHeight = maxHeight;
	}

	public void toggleOpen() {
		if (open) {
			open = false;
			SessionVars.setOpenedLedger(null);
		} else {
			open = true;
			SessionVars.setOpenedLedger(this.getClass());
		}
	}

	public boolean isVisible() {
		return true;
	}

	public boolean isOpen() {
		return this.open;
	}

	protected boolean isFullyOpened() {
		return currentWidth >= maxWidth;
	}

	public void onGuiClosed() {

	}

	protected void drawBackground(PoseStack transform, int y, int x) {
		float colorR = (overlayColor >> 16 & 255) / 255.0F;
		float colorG = (overlayColor >> 8 & 255) / 255.0F;
		float colorB = (overlayColor & 255) / 255.0F;

		RenderSystem.setShaderColor(colorR, colorG, colorB, 1.0F);
		RenderSystem.setShaderTexture(0, texture);

		int height = getHeight();
		int width = getWidth();

		manager.gui.blit(transform, x, y + 4, 0, 256 - height + 4, 4, height - 4); // left edge
		manager.gui.blit(transform, x + 4, y, 256 - width + 4, 0, width - 4, 4); // top edge
		manager.gui.blit(transform, x, y, 0, 0, 4, 4); // top left corner

		manager.gui.blit(transform, x + 4, y + 4, 256 - width + 4, 256 - height + 4, width - 4, height - 4); // body + bottom + right

		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
	}

	protected void drawSprite(PoseStack transform, TextureAtlasSprite sprite, int x, int y) {
		drawSprite(transform, sprite, x, y, TextureManagerForestry.getInstance().getGuiTextureMap());
	}

	protected void drawSprite(PoseStack transform, TextureAtlasSprite sprite, int x, int y, ResourceLocation textureMap) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
		RenderSystem.setShaderTexture(0, textureMap);
		GuiComponent.blit(transform, x, y, manager.gui.getBlitOffset(), 16, 16, sprite);
	}

	protected int drawHeader(PoseStack transform, String string, int x, int y) {
		return drawShadowText(transform, string, x, y, fontColorHeader);
	}

	protected int drawSubheader(PoseStack transform, String string, int x, int y) {
		return drawShadowText(transform, string, x, y, fontColorSubheader);
	}

	protected int drawShadowText(PoseStack transform, String string, int x, int y, int color) {
		return drawSplitText(transform, string, x, y, maxTextWidth, color, true);
	}

	protected int drawSplitText(PoseStack transform, String string, int x, int y, int width) {
		return drawSplitText(transform, string, x, y, width, fontColorText, false);
	}

	protected int drawSplitText(PoseStack transform, String string, int x, int y, int width, int color, boolean shadow) {
		int originalY = y;
		Minecraft minecraft = Minecraft.getInstance();
		List<FormattedCharSequence> strings = minecraft.font.split(Component.literal(string), width);
		for (FormattedCharSequence obj : strings) {
			if (shadow) {
				minecraft.font.drawShadow(transform, obj, x, y, color);
			} else {
				minecraft.font.draw(transform, obj, x, y, color);
			}
			y += minecraft.font.lineHeight;
		}
		return y - originalY;
	}

	protected int drawText(PoseStack transform, String string, int x, int y) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.font.draw(transform, string, x, y, fontColorText);
		return minecraft.font.lineHeight;
	}
}
