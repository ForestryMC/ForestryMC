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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.core.config.SessionVars;
import forestry.core.errors.FakeErrorSource;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.ToolTip;

public class LedgerManager {
	private final List<Ledger> ledgers = new ArrayList<>();
	private final List<ErrorLedger> errorLedgers = new ArrayList<>();

	private IErrorSource errorSource;
	private final int maxWidth;

	public final GuiForestry gui;

	public LedgerManager(GuiForestry gui, int maxWidth) {
		this.gui = gui;
		this.errorSource = FakeErrorSource.instance;
		this.maxWidth = maxWidth;
	}

	public void add(IErrorSource errorSource) {
		if (errorSource == null) {
			return;
		}

		this.errorSource = errorSource;
		int maxErrorLedgerCount = (gui.getSizeY() - 10) / Ledger.minHeight;
		for (int i = 0; i < maxErrorLedgerCount; i++) {
			errorLedgers.add(new ErrorLedger(this));
		}
	}

	public void add(Ledger ledger) {
		this.ledgers.add(ledger);
		if (SessionVars.getOpenedLedger() != null && ledger.getClass().equals(SessionVars.getOpenedLedger())) {
			ledger.setFullyOpen();
		}
	}

	public void onGuiClosed() {
		for (Ledger ledger : ledgers) {
			ledger.onGuiClosed();
		}
	}

	/**
	 * Inserts a ledger into the next-to-last position.
	 */
	public void insert(Ledger ledger) {
		this.ledgers.add(ledgers.size() - 1, ledger);
	}

	private Ledger getAtPosition(int mX, int mY) {
		if (ledgers.size() > 0) {
			final int xShift = ((gui.width - gui.getSizeX()) / 2) + gui.getSizeX();
			int yShift = ((gui.height - gui.getSizeY()) / 2) + 8;

			for (Ledger ledger : ledgers) {
				if (!ledger.isVisible()) {
					continue;
				}

				ledger.currentShiftX = xShift;
				ledger.currentShiftY = yShift;
				if (ledger.intersectsWith(mX, mY)) {
					return ledger;
				}

				yShift += ledger.getHeight();
			}
		}

		final int xShiftError = ((gui.width - gui.getSizeX()) / 2);
		int yShiftError = ((gui.height - gui.getSizeY()) / 2) + 8;

		for (ErrorLedger errorLedger : errorLedgers) {
			if (!errorLedger.isVisible()) {
				continue;
			}

			errorLedger.currentShiftX = xShiftError - errorLedger.getWidth();
			errorLedger.currentShiftY = yShiftError;
			if (errorLedger.intersectsWith(mX, mY)) {
				return errorLedger;
			}

			yShiftError += errorLedger.getHeight();
		}

		return null;
	}

	public void drawLedgers() {
		int yPos = 8;
		for (Ledger ledger : ledgers) {

			ledger.update();
			if (!ledger.isVisible()) {
				continue;
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			ledger.draw(gui.getSizeX(), yPos);
			yPos += ledger.getHeight();
		}

		List<IErrorState> errorStates = new ArrayList<>(errorSource.getErrorStates());

		yPos = 8;
		int index = 0;
		for (ErrorLedger errorLedger : errorLedgers) {
			if (index >= errorStates.size()) {
				errorLedger.setState(null);
				continue;
			}
			IErrorState errorState = errorStates.get(index++);
			errorLedger.setState(errorState);

			errorLedger.update();
			if (!errorLedger.isVisible()) {
				continue;
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			errorLedger.draw(-errorLedger.getWidth(), yPos);
			yPos += errorLedger.getHeight();
		}
	}

	public void drawTooltips(int mouseX, int mouseY) {
		Ledger ledger = getAtPosition(mouseX, mouseY);
		if (ledger != null) {
			ToolTip toolTip = new ToolTip();
			toolTip.add(ledger.getTooltip());
			GuiUtil.drawToolTips(gui, toolTip, mouseX, mouseY);
		}
	}

	public void handleMouseClicked(int x, int y, int mouseButton) {

		if (mouseButton == 0) {

			Ledger ledger = this.getAtPosition(x, y);

			// Default action only if the mouse click was not handled by the
			// ledger itself.
			if (ledger != null && !ledger.handleMouseClicked(x, y, mouseButton)) {

				List<? extends Ledger> toggleLedgers;
				if (ledgers.contains(ledger)) {
					toggleLedgers = ledgers;
				} else {
					toggleLedgers = errorLedgers;
				}

				for (Ledger other : toggleLedgers) {
					if (other != ledger && other.isOpen()) {
						other.toggleOpen();
					}
				}
				ledger.toggleOpen();
			}
		}

	}

	public boolean ledgerOverlaps(int x, int y, int width, int height) {
		return getAtPosition(x + width, y + height) != null || getAtPosition(x + width, y) != null || getAtPosition(x, y + height) != null || getAtPosition(x, y) != null;
	}

	public int getMaxWidth() {
		return maxWidth;
	}
}
