/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import buildcraft.api.power.PowerHandler;

import forestry.core.interfaces.IPowerHandler;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class PowerLedger extends Ledger {

	IPowerHandler tile;

	public PowerLedger(LedgerManager manager, IPowerHandler tile) {
		super(manager);
		this.tile = tile;
		maxHeight = 94;
		overlayColor = manager.gui.fontColor.get("ledger.power.background");
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(TextureManager.getInstance().getDefault("misc/energy"), x + 3, y + 4);

		if (!isFullyOpened())
			return;

		PowerHandler handler = tile.getPowerHandler();
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.energy"), x + 22, y + 8, manager.gui.fontColor.get("ledger.power.header"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.stored") + ":", x + 22, y + 20,
				manager.gui.fontColor.get("ledger.power.subheader"));
		manager.minecraft.fontRenderer.drawString(handler.getEnergyStored() + " MJ", x + 22, y + 32, manager.gui.fontColor.get("ledger.power.text"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.maxenergy") + ":", x + 22, y + 44,
				manager.gui.fontColor.get("ledger.power.subheader"));
		manager.minecraft.fontRenderer.drawString(handler.getMaxEnergyStored() + " MJ", x + 22, y + 56, manager.gui.fontColor.get("ledger.power.text"));
		manager.minecraft.fontRenderer.drawStringWithShadow(StringUtil.localize("gui.maxenergyreceive") + ":", x + 22, y + 68,
				manager.gui.fontColor.get("ledger.power.subheader"));
		manager.minecraft.fontRenderer.drawString(handler.getMaxEnergyReceived() + " MJ", x + 22, y + 80, manager.gui.fontColor.get("ledger.power.text"));

	}

	@Override
	public String getTooltip() {
		return tile.getPowerHandler().getEnergyStored() + " MJ";
	}

}
