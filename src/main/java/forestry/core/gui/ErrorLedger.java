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

import forestry.api.core.IErrorState;
import forestry.core.EnumErrorCode;
import forestry.core.interfaces.IErrorSource;
import forestry.core.utils.StringUtil;

/**
 * A ledger displaying error messages and help text.
 */
public class ErrorLedger extends Ledger {

	private final IErrorSource tile;

	public ErrorLedger(LedgerManager manager, IErrorSource tile) {
		super(manager);
		this.tile = tile;
		maxHeight = 72;
		overlayColor = manager.gui.fontColor.get("ledger.error.background");
	}

	@Override
	public void draw(int x, int y) {
		IErrorState state = tile.getErrorState();
		if (state == EnumErrorCode.OK) {
			return;
		}

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(state.getIcon(), x + 3, y + 4);

		// Write description if fully opened
		if (isFullyOpened()) {
			// fontRendererObj.drawStringWithShadow(getTooltip(), x + 22, y +
			// 8, 0xe1c92f);
			manager.minecraft.fontRendererObj.drawStringWithShadow(getTooltip(), x + 22, y + 8, manager.gui.fontColor.get("ledger.error.header"));
			manager.minecraft.fontRendererObj.drawSplitString(StringUtil.localize(tile.getErrorState().getHelp()), x + 22, y + 20, maxWidth - 28,
					manager.gui.fontColor.get("ledger.error.text"));
		}
	}

	@Override
	public boolean isVisible() {
		IErrorState state = tile.getErrorState();
		return state != EnumErrorCode.OK;
	}

	@Override
	public String getTooltip() {
		return StringUtil.localize(tile.getErrorState().getDescription());
	}

}
