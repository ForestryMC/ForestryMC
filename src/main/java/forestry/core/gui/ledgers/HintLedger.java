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
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.Translator;

public class HintLedger extends Ledger {

	private final String hintString;
	private final String hintTooltip;

	public HintLedger(LedgerManager manager, List<String> hints) {
		super(manager, "hint");
		int position = new Random().nextInt(hints.size());
		String hint = hints.get(position);

		hintString = Translator.translateToLocal("for.hints." + hint + ".desc");
		hintTooltip = Translator.translateToLocal("for.hints." + hint + ".tag");

		Minecraft minecraft = Minecraft.getMinecraft();
		FontRenderer fontRenderer = minecraft.fontRenderer;
		int lineCount = fontRenderer.listFormattedStringToWidth(hintString, maxTextWidth).size();
		maxHeight = (lineCount + 1) * fontRenderer.FONT_HEIGHT + 20;
	}

	@Override
	public void draw(int x, int y) {

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawSprite(TextureManagerForestry.getInstance().getDefault("misc/hint"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		drawHeader(Translator.translateToLocal("for.gui.didyouknow") + '?', x + 22, y + 8);
		drawSplitText(hintString, x + 12, y + 20, maxTextWidth);
	}

	@Override
	public String getTooltip() {
		return hintTooltip;
	}
}
