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

import forestry.core.config.Defaults;
import forestry.core.config.SessionVars;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.ForestryResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Side ledger for guis
 */
public abstract class Ledger {
	
	private static final int OPEN_SPEED = 8;
	private static final ResourceLocation ledgerTexture = new ForestryResource(Defaults.TEXTURE_PATH_GUI + "/ledger.png");
	
	protected final LedgerManager manager;

	private boolean open;

	protected int overlayColor = 0xffffff;

	public int currentShiftX = 0;
	public int currentShiftY = 0;

	protected int limitWidth = 128;
	protected int maxWidth = 124;
	protected int minWidth = 24;
	protected int currentWidth = minWidth;

	protected int maxHeight = 24;
	protected int minHeight = 24;
	protected int currentHeight = minHeight;

	public Ledger(LedgerManager manager) {
		this.manager = manager;
	}

	public void update() {
		// Width
		if (open && currentWidth < maxWidth)
			currentWidth += OPEN_SPEED;
		else if (!open && currentWidth > minWidth) {
			currentWidth -= OPEN_SPEED;
			currentWidth = currentWidth > minWidth ? currentWidth : minWidth;
		}

		// Height
		if (open && currentHeight < maxHeight)
			currentHeight += OPEN_SPEED;
		else if (!open && currentHeight > minHeight) {
			currentHeight -= OPEN_SPEED;
			currentHeight = currentHeight > minHeight ? currentHeight : minHeight;
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

	public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {
		return mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + getHeight();
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

		Proxies.common.bindTexture(ledgerTexture);
		
		manager.gui.drawTexturedModalRect(x, y, 0, 256 - currentHeight, 4, currentHeight);
		manager.gui.drawTexturedModalRect(x + 4, y, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
		manager.gui.drawTexturedModalRect(x, y, 0, 0, 4, 4);

		manager.gui.drawTexturedModalRect(x + 4, y + 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

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
