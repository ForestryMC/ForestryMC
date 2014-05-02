/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import java.util.Random;

import forestry.core.interfaces.IHintSource;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class HintLedger extends Ledger {

	int position;
	String[] hints;

	public HintLedger(LedgerManager manager, IHintSource tile) {
		super(manager);
		this.hints = tile.getHints();
		maxHeight = 96;
		position = new Random().nextInt(hints.length);
		overlayColor = manager.gui.fontColor.get("ledger.hint.background");
	}

	@Override
	public void draw(int x, int y) {

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(TextureManager.getInstance().getDefault("misc/hint"), x + 3, y + 4);

		if (!isFullyOpened())
			return;

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.didyouknow") + "?", x + 22, y + 8,
				manager.gui.fontColor.get("ledger.hint.header"));
		manager.minecraft.fontRenderer.drawSplitString(StringUtil.localize("hints." + hints[position] + ".desc"), x + 22, y + 20, maxWidth - 28,
				manager.gui.fontColor.get("ledger.hint.text"));

	}

	@Override
	public String getTooltip() {
		return StringUtil.localize("hints." + hints[position] + ".tag");
	}

}
