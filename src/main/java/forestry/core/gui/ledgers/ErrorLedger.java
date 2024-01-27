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

import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

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
			//TODO - textcomponent
			int lineHeight = StringUtil.getLineHeight(maxTextWidth, getTooltip(), Component.translatable(state.getUnlocalizedHelp()));
			maxHeight = lineHeight + 20;
		}
	}

	@Override
	public void draw(PoseStack transform, int y, int x) {
		if (state == null) {
			return;
		}

		// Draw background
		drawBackground(transform, y, x);
		y += 4;

		int xIcon = x + 5;
		int xBody = x + 14;
		int xHeader = x + 24;

		// Draw sprite
		drawSprite(transform, state.getSprite(), xIcon, y);
		y += 4;

		// Write description if fully opened
		if (isFullyOpened()) {
			//TODO textcomponent
			y += drawHeader(transform, getTooltip().getString(), xHeader, y);
			y += 4;

			String helpString = Translator.translateToLocal(state.getUnlocalizedHelp());
			drawSplitText(transform, helpString, xBody, y, maxTextWidth);
		}
	}

	@Override
	public boolean isVisible() {
		return state != null;
	}

	@Override
	public Component getTooltip() {
		if (state == null) {
			return Component.literal("");
		}
		return Component.translatable(state.getUnlocalizedDescription());
	}

}
