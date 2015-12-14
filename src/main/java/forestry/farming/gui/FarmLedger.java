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
package forestry.farming.gui;

import net.minecraft.init.Items;

import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.utils.StringUtil;

public class FarmLedger extends Ledger {
	private final IFarmLedgerDelegate delegate;

	public FarmLedger(LedgerManager ledgerManager, IFarmLedgerDelegate delegate) {
		super(ledgerManager, "farm");
		this.delegate = delegate;

		int titleHeight = StringUtil.getLineHeight(maxTextWidth, getTooltip());
		this.maxHeight = titleHeight + 110;
	}

	@Override
	public void draw(int x, int y) {

		// Draw background
		drawBackground(x, y);
		y += 4;

		int xIcon = x + 3;
		int xBody = x + 10;
		int xHeader = x + 22;

		// Draw icon
		drawIcon(Items.water_bucket.getIconFromDamage(0), xIcon, y);
		y += 4;

		if (!isFullyOpened()) {
			return;
		}

		y += drawHeader(StringUtil.localize("gui.hydration"), xHeader, y);
		y += 4;

		y += drawSubheader(StringUtil.localize("gui.hydr.heat") + ':', xBody, y);
		y += 3;
		y += drawText(StringUtil.floatAsPercent(delegate.getHydrationTempModifier()), xBody, y);
		y += 3;

		y += drawSubheader(StringUtil.localize("gui.hydr.humid") + ':', xBody, y);
		y += 3;
		y += drawText(StringUtil.floatAsPercent(delegate.getHydrationHumidModifier()), xBody, y);
		y += 3;

		y += drawSubheader(StringUtil.localize("gui.hydr.rainfall") + ':', xBody, y);
		y += 3;
		y += drawText(StringUtil.floatAsPercent(delegate.getHydrationRainfallModifier()) + " (" + delegate.getDrought() + " d)", xBody, y);
		y += 3;

		y += drawSubheader(StringUtil.localize("gui.hydr.overall") + ':', xBody, y);
		y += 3;
		drawText(StringUtil.floatAsPercent(delegate.getHydrationModifier()), xBody, y);
	}

	@Override
	public String getTooltip() {
		float hydrationModifier = delegate.getHydrationModifier();
		return StringUtil.floatAsPercent(hydrationModifier) + ' ' + StringUtil.localize("gui.hydration");
	}
}