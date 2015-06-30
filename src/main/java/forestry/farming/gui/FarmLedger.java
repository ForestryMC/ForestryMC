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

import forestry.core.gui.Ledger;
import forestry.core.gui.LedgerManager;
import forestry.core.utils.StringUtil;

public class FarmLedger extends Ledger {
	private final IFarmLedgerDelegate delegate;


	public FarmLedger(LedgerManager ledgerManager, IFarmLedgerDelegate delegate) {
		super(ledgerManager, "farm");
		this.maxHeight = 118;
		this.delegate = delegate;
	}

	@Override
	public void draw(int x, int y) {

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(Items.water_bucket.getIconFromDamage(0), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		drawHeader(StringUtil.localize("gui.hydration"), x + 22, y + 8);

		drawSubheader(StringUtil.localize("gui.hydr.heat") + ':', x + 22, y + 20);
		drawText(StringUtil.floatAsPercent(delegate.getHydrationTempModifier()), x + 22, y + 32);

		drawSubheader(StringUtil.localize("gui.hydr.humid") + ':', x + 22, y + 44);
		drawText(StringUtil.floatAsPercent(delegate.getHydrationHumidModifier()), x + 22, y + 56);

		drawSubheader(StringUtil.localize("gui.hydr.rainfall") + ':', x + 22, y + 68);
		drawText(StringUtil.floatAsPercent(delegate.getHydrationRainfallModifier()) + " (" + delegate.getDrought() + " d)", x + 22, y + 80);

		drawSubheader(StringUtil.localize("gui.hydr.overall") + ':', x + 22, y + 92);
		drawText(StringUtil.floatAsPercent(delegate.getHydrationModifier()), x + 22, y + 104);
	}

	@Override
	public String getTooltip() {
		return StringUtil.floatAsPercent(delegate.getHydrationModifier()) + ' ' + StringUtil.localize("gui.hydration");
	}
}