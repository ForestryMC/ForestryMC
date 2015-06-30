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

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.core.interfaces.IHintSource;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class HintLedger extends Ledger {

	private final String hintString;
	private final String hintTooltip;

	public HintLedger(LedgerManager manager, IHintSource tile) {
		super(manager, "hint");
		String[] hints = tile.getHints();
		int position = new Random().nextInt(hints.length);

		hintString = StringUtil.localize("hints." + hints[position] + ".desc");
		hintTooltip = StringUtil.localize("hints." + hints[position] + ".tag");

		Minecraft minecraft = Proxies.common.getClientInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;
		int lineCount = fontRenderer.listFormattedStringToWidth(hintString, maxWidth - 28).size();
		maxHeight = (lineCount + 1) * fontRenderer.FONT_HEIGHT + 20;
	}

	@Override
	public void draw(int x, int y) {

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(TextureManager.getInstance().getDefault("misc/hint"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		drawHeader(StringUtil.localize("gui.didyouknow") + '?', x + 22, y + 8);
		drawSplitText(hintString, x + 22, y + 20, maxWidth - 28);
	}

	@Override
	public String getTooltip() {
		return hintTooltip;
	}
}
