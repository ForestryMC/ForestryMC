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

import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.core.tiles.IClimatised;
import forestry.core.utils.StringUtil;

/**
 * A ledger containing climate information.
 */
public class ClimateLedger extends Ledger {

	private final IClimatised tile;

	public ClimateLedger(LedgerManager manager, IClimatised tile) {
		super(manager, "climate");
		this.tile = tile;
		maxHeight = 72;
	}

	@Override
	public void draw(int x, int y) {

		EnumTemperature temperature = tile.getTemperature();

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(temperature.getIcon(), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		drawHeader(StringUtil.localize("gui.climate"), x + 22, y + 8);

		drawSubheader(StringUtil.localize("gui.temperature") + ':', x + 22, y + 20);
		drawText(AlleleManager.climateHelper.toDisplay(temperature) + ' ' + StringUtil.floatAsPercent(tile.getExactTemperature()), x + 22, y + 32);

		drawSubheader(StringUtil.localize("gui.humidity") + ':', x + 22, y + 44);
		drawText(AlleleManager.climateHelper.toDisplay(tile.getHumidity()) + ' ' + StringUtil.floatAsPercent(tile.getExactHumidity()), x + 22, y + 56);
	}

	@Override
	public String getTooltip() {
		return "T: " + AlleleManager.climateHelper.toDisplay(tile.getTemperature()) + " / H: " + AlleleManager.climateHelper.toDisplay(tile.getHumidity());
	}

}
