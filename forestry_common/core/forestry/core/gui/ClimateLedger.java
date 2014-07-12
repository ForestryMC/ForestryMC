/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.core.interfaces.IClimatised;
import forestry.core.utils.StringUtil;

/**
 * A ledger containing climate information.
 */
public class ClimateLedger extends Ledger {

	IClimatised tile;

	public ClimateLedger(LedgerManager manager, IClimatised tile) {
		super(manager);
		this.tile = tile;
		maxHeight = 72;
		overlayColor = manager.gui.fontColor.get("ledger.climate.background");
	}

	@Override
	public void draw(int x, int y) {

		EnumTemperature temperature = tile.getTemperature();

		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(temperature.getIcon(), x + 3, y + 4);

		if (!isFullyOpened())
			return;

		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.climate"), x + 22, y + 8,
				manager.gui.fontColor.get("ledger.climate.header"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.temperature") + ":", x + 22, y + 20,
				manager.gui.fontColor.get("ledger.climate.subheader"));
		manager.minecraft.fontRenderer.drawString(AlleleManager.climateHelper.toDisplay(temperature) + " " + StringUtil.floatAsPercent(tile.getExactTemperature()), x + 22,
				y + 32, manager.gui.fontColor.get("ledger.climate.text"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.humidity") + ":", x + 22, y + 44,
				manager.gui.fontColor.get("ledger.climate.subheader"));
		manager.minecraft.fontRenderer.drawString(AlleleManager.climateHelper.toDisplay(tile.getHumidity()) + " " + StringUtil.floatAsPercent(tile.getExactHumidity()),
				x + 22, y + 56, manager.gui.fontColor.get("ledger.climate.text"));
	}

	@Override
	public String getTooltip() {
		return "T: " + AlleleManager.climateHelper.toDisplay(tile.getTemperature()) + " / H: " + AlleleManager.climateHelper.toDisplay(tile.getHumidity());
	}

}
