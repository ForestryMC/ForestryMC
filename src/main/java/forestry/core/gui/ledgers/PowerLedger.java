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

import forestry.core.interfaces.IPowerHandler;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import forestry.energy.EnergyManager;

public class PowerLedger extends Ledger {

	private final IPowerHandler tile;

	public PowerLedger(LedgerManager manager, IPowerHandler tile) {
		super(manager, "power");
		this.tile = tile;
		maxHeight = 94;
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawIcon(TextureManager.getInstance().getDefault("misc/energy"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		EnergyManager energyManager = tile.getEnergyManager();

		drawHeader(StringUtil.localize("gui.energy"), x + 22, y + 8);

		drawSubheader(StringUtil.localize("gui.stored") + ':', x + 22, y + 20);
		drawText(energyManager.getTotalEnergyStored() + " RF", x + 22, y + 32);

		drawSubheader(StringUtil.localize("gui.maxenergy") + ':', x + 22, y + 44);
		drawText(energyManager.getMaxEnergyStored() + " RF", x + 22, y + 56);

		drawSubheader(StringUtil.localize("gui.maxenergyreceive") + ':', x + 22, y + 68);
		drawText(energyManager.getMaxEnergyReceived() + " RF", x + 22, y + 80);
	}

	@Override
	public String getTooltip() {
		return tile.getEnergyManager().getTotalEnergyStored() + " RF";
	}

}
