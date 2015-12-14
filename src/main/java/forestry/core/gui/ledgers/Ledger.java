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
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.SessionVars;
import forestry.core.proxy.Proxies;
import forestry.core.render.ForestryResource;
import forestry.core.render.SpriteSheet;

/**
 * Side ledger for guis
 */
public abstract class Ledger {

	protected static final int minWidth = 24;
	public static final int minHeight = 24;
	protected final int maxWidth;
	protected final int maxTextWidth;
	protected int maxHeight = 24;

	private static final ResourceLocation ledgerTextureRight = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/ledger.png");
	private static final ResourceLocation ledgerTextureLeft = new ForestryResource(Constants.TEXTURE_PATH_GUI + "/ledgerLeft.png");
	
	private final LedgerManager manager;

	private final int fontColorHeader;
	private final int fontColorText;
	private final int fontColorSubheader;
	private final int overlayColor;

	private boolean open;

	public int currentShiftX = 0;
	public int currentShiftY = 0;

	protected float currentWidth = minWidth;
	protected float currentHeight = minHeight;

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

	public abstract void draw(int x, int y);

	public abstract String getTooltip();

	public boolean handleMouseClicked(int x, int y, int mouseButton) {
		return false;
	}

	public boolean intersectsWith(int mouseX, int mouseY) {
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

	protected void drawBackground(int x, int y) {

		float colorR = (overlayColor >> 16 & 255) / 255.0F;
		float colorG = (overlayColor >> 8 & 255) / 255.0F;
		float colorB = (overlayColor & 255) / 255.0F;

		GL11.glColor4f(colorR, colorG, colorB, 1.0F);

		Proxies.render.bindTexture(texture);

		int height = getHeight();
		int width = getWidth();

		manager.gui.drawTexturedModalRect(x, y + 4, 0, 256 - height + 4, 4, height - 4); // left edge
		manager.gui.drawTexturedModalRect(x + 4, y, 256 - width + 4, 0, width - 4, 4); // top edge
		manager.gui.drawTexturedModalRect(x, y, 0, 0, 4, 4); // top left corner

		manager.gui.drawTexturedModalRect(x + 4, y + 4, 256 - width + 4, 256 - height + 4, width - 4, height - 4); // body + bottom + right

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
	}

	protected void drawIcon(IIcon icon, int x, int y) {
		if (icon != null) {
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			Proxies.render.bindTexture(SpriteSheet.ITEMS);
			manager.gui.drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
		}
	}

	protected int drawHeader(String string, int x, int y) {
		return drawShadowText(string, x, y, fontColorHeader);
	}

	protected int drawSubheader(String string, int x, int y) {
		return drawShadowText(string, x, y, fontColorSubheader);
	}

	protected int drawShadowText(String string, int x, int y, int color) {
		return drawSplitText(string, x, y, maxTextWidth, color, true);
	}

	protected int drawSplitText(String string, int x, int y, int width) {
		return drawSplitText(string, x, y, width, fontColorText, false);
	}

	protected int drawSplitText(String string, int x, int y, int width, int color, boolean shadow) {
		int originalY = y;
		Minecraft minecraft = Proxies.common.getClientInstance();
		List strings = minecraft.fontRenderer.listFormattedStringToWidth(string, width);
		for (Object obj : strings) {
			if (obj instanceof String) {
				minecraft.fontRenderer.drawString((String) obj, x, y, color, shadow);
				y += minecraft.fontRenderer.FONT_HEIGHT;
			}
		}
		return y - originalY;
	}

	protected int drawText(String string, int x, int y) {
		Minecraft minecraft = Proxies.common.getClientInstance();
		minecraft.fontRenderer.drawString(string, x, y, fontColorText);
		return minecraft.fontRenderer.FONT_HEIGHT;
	}
}
