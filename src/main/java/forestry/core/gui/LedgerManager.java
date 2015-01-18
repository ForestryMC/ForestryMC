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

import forestry.core.config.SessionVars;
import forestry.core.gadgets.TileForestry;
import forestry.core.proxy.Proxies;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class LedgerManager {

	public final GuiForestry<? extends TileForestry> gui;
	public final Minecraft minecraft;

	protected final ArrayList<Ledger> ledgers = new ArrayList<Ledger>();

	public LedgerManager(GuiForestry<? extends TileForestry> gui) {
		this.gui = gui;
		this.minecraft = Proxies.common.getClientInstance();
	}

	public void add(Ledger ledger) {
		this.ledgers.add(ledger);
		if (SessionVars.getOpenedLedger() != null && ledger.getClass().equals(SessionVars.getOpenedLedger()))
			ledger.setFullyOpen();
	}

	/**
	 * Inserts a ledger into the next-to-last position.
	 */
	public void insert(Ledger ledger) {
		this.ledgers.add(ledgers.size() - 1, ledger);
	}

	protected Ledger getAtPosition(int mX, int mY) {

		int xShift = ((gui.width -  gui.getSizeX()) / 2) +  gui.getSizeX();
		int yShift = ((gui.height -  gui.getSizeY()) / 2) + 8;

		for (Ledger ledger : ledgers) {
			if (!ledger.isVisible())
				continue;

			ledger.currentShiftX = xShift;
			ledger.currentShiftY = yShift;
			if (ledger.intersectsWith(mX, mY, xShift, yShift))
				return ledger;

			yShift += ledger.getHeight();
		}

		return null;
	}

	protected void drawLedgers() {

		int yPos = 8;
		for (Ledger ledger : ledgers) {

			ledger.update();
			if (!ledger.isVisible())
				continue;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			ledger.draw(gui.getSizeX(), yPos);
			yPos += ledger.getHeight();
		}
	}

	protected void drawTooltips(int mouseX, int mouseY) {

		Ledger ledger = getAtPosition(mouseX, mouseY);
		if (ledger != null) {
			int startX = mouseX - ((gui.width - gui.getSizeX()) / 2) + 12;
			int startY = mouseY - ((gui.height - gui.getSizeY()) / 2) - 12;

			String tooltip = ledger.getTooltip();
			int textWidth = minecraft.fontRenderer.getStringWidth(tooltip);
			gui.drawGradientRect(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3, 0xc0000000, 0xc0000000);
			minecraft.fontRenderer.drawStringWithShadow(tooltip, startX, startY, -1);
		}

	}

	public void handleMouseClicked(int x, int y, int mouseButton) {

		if (mouseButton == 0) {

			Ledger ledger = this.getAtPosition(x, y);

			// Default action only if the mouse click was not handled by the
			// ledger itself.
			if (ledger != null && !ledger.handleMouseClicked(x, y, mouseButton)) {

				for (Ledger other : ledgers)
					if (other != ledger && other.isOpen())
						other.toggleOpen();
				ledger.toggleOpen();
			}
		}

	}

	public boolean ledgerOverlaps(int x, int y, int width, int height) {
		return getAtPosition(x + width, y + height) != null || getAtPosition(x + width, y) != null || getAtPosition(x, y + height) != null || getAtPosition(x, y) != null;
	}

}
