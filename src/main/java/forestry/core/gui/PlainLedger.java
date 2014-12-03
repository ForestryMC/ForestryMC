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

import net.minecraft.util.IIcon;

/**
 * A simple ledger displaying an icon and one-line description.
 */
public class PlainLedger extends Ledger {

	private final IIcon icon;
	private final String text;

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
