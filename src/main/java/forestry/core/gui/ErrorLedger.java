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

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.api.core.IErrorState;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

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
			String helpString = StringUtil.localize(state.getHelp());
			Minecraft minecraft = Proxies.common.getClientInstance();
			FontRenderer fontRenderer = minecraft.fontRenderer;
			int lineCount = fontRenderer.listFormattedStringToWidth(helpString, maxWidth - 28).size();
			maxHeight = (lineCount + 1) * fontRenderer.FONT_HEIGHT + 20;
		}
	}

	@Override
	public void draw(int x, int y) {
		if (state == null) {
			return;
		}

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(state.getIcon(), x + 5, y + 4);

		// Write description if fully opened
		if (isFullyOpened()) {
			drawHeader(getTooltip(), x + 24, y + 8);

			String helpString = StringUtil.localize(state.getHelp());
			drawSplitText(helpString, x + 24, y + 20, maxWidth - 28);
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
		return StringUtil.localize(state.getDescription());
	}

}
