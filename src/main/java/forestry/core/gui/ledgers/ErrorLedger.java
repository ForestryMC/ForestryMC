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

import javax.annotation.Nullable;

import forestry.api.core.IErrorState;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

/**
 * A ledger displaying error messages and help text.
 */
public class ErrorLedger extends Ledger {

	@Nullable
	private IErrorState state;

	public ErrorLedger(LedgerManager manager) {
		super(manager, "error", false);
		maxHeight = 72;
	}

	public void setState(@Nullable IErrorState state) {
		this.state = state;
		if (state != null) {
			int lineHeight = StringUtil.getLineHeight(maxTextWidth, getTooltip(), Translator.translateToLocal(state.getUnlocalizedHelp()));
			maxHeight = lineHeight + 20;
		}
	}

	@Override
	public void draw(int x, int y) {
		if (state == null) {
			return;
		}

		// Draw background
		drawBackground(x, y);
		y += 4;

		int xIcon = x + 5;
		int xBody = x + 14;
		int xHeader = x + 24;

		// Draw sprite
		drawSprite(state.getSprite(), xIcon, y);
		y += 4;

		// Write description if fully opened
		if (isFullyOpened()) {
			y += drawHeader(getTooltip(), xHeader, y);
			y += 4;

			String helpString = Translator.translateToLocal(state.getUnlocalizedHelp());
			drawSplitText(helpString, xBody, y, maxTextWidth);
		}
	}

	@Override
	public boolean isVisible() {
		return state != null;
	}

	@Override
	public String getTooltip() {
		if (state == null) {
			return "";
		}
		return Translator.translateToLocal(state.getUnlocalizedDescription());
	}

}
