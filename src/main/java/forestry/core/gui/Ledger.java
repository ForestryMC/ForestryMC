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
package forestry.core.gui;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.SessionVars;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.ForestryResource;

/**
 * Side ledger for guis
 */
public abstract class Ledger {
	
	protected static final int maxWidth = 124;
	protected static final int minWidth = 24;
	public static final int minHeight = 24;

	private static final ResourceLocation ledgerTextureRight = new ForestryResource(Defaults.TEXTURE_PATH_GUI + "/ledger.png");
	private static final ResourceLocation ledgerTextureLeft = new ForestryResource(Defaults.TEXTURE_PATH_GUI + "/ledgerLeft.png");
	
	protected final LedgerManager manager;

	private boolean open;

	protected int overlayColor = 0xffffff;

	public int currentShiftX = 0;
	public int currentShiftY = 0;
	protected int currentWidth = minWidth;
	protected int maxHeight = 24;

	protected int currentHeight = minHeight;

	protected final ResourceLocation texture;

	public Ledger(LedgerManager manager) {
		this(manager, true);
	}

	public Ledger(LedgerManager manager, boolean rightSide) {
		this.manager = manager;
		if (rightSide) {
			texture = ledgerTextureRight;
		} else {
			texture = ledgerTextureLeft;
		}
	}

	public void update() {
		// Width
		if (open && currentWidth < maxWidth) {
			currentWidth += Config.guiTabSpeed;
			if (currentWidth > maxWidth) {
				currentWidth = maxWidth;
			}
		} else if (!open && currentWidth > minWidth) {
			currentWidth -= Config.guiTabSpeed;
			if (currentWidth < minWidth) {
				currentWidth = minWidth;
			}
		}

		// Height
		if (open && currentHeight < maxHeight) {
			currentHeight += Config.guiTabSpeed;
			if (currentHeight > maxHeight) {
				currentHeight = maxHeight;
			}
		} else if (!open && currentHeight > minHeight) {
			currentHeight -= Config.guiTabSpeed;
			if (currentHeight < minHeight) {
				currentHeight = minHeight;
			}
		}
	}

	public int getHeight() {
		return currentHeight;
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

	protected void drawBackground(int x, int y) {

		float colorR = (overlayColor >> 16 & 255) / 255.0F;
		float colorG = (overlayColor >> 8 & 255) / 255.0F;
		float colorB = (overlayColor & 255) / 255.0F;

		GL11.glColor4f(colorR, colorG, colorB, 1.0F);

		Proxies.common.bindTexture(texture);

		manager.gui.drawTexturedModalRect(x, y + 4, 0, 256 - currentHeight + 4, 4, currentHeight - 4); // left edge
		manager.gui.drawTexturedModalRect(x + 4, y, 256 - currentWidth + 4, 0, currentWidth - 4, 4); // top edge
		manager.gui.drawTexturedModalRect(x, y, 0, 0, 4, 4); // top left corner

		manager.gui.drawTexturedModalRect(x + 4, y + 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4); // body + bottom + right

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
	}

	protected void drawIcon(IIcon icon, int x, int y) {

		if (icon != null) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			Proxies.common.bindTexture(SpriteSheet.ITEMS);
			manager.gui.drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
			GL11.glEnable(GL11.GL_LIGHTING);
		}

	}
}
