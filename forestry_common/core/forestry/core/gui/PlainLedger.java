/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import net.minecraft.util.IIcon;

/**
 * A simple ledger displaying an icon and one-line description.
 */
public class PlainLedger extends Ledger {

	private IIcon icon;
	private String text;

	public PlainLedger(LedgerManager manager, IIcon icon, String text) {
		super(manager);
		this.icon = icon;
		this.text = text;
	}

	@Override
	public void draw(int x, int y) {

		// Correct maximum width
		int textWidth = manager.minecraft.fontRenderer.getStringWidth(getTooltip());
		this.maxWidth = textWidth + 24 + 4;

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(icon, x + 3, y + 4);

		// Draw description
		if (isFullyOpened())
			manager.minecraft.fontRenderer.drawString(getTooltip(), x + 22, y + 8, 0x000000);
	}

	@Override
	public String getTooltip() {
		return text;
	}
}
