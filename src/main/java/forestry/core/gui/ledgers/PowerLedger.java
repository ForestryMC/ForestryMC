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

import forestry.core.render.TextureManager;
import forestry.core.tiles.IPowerHandler;
import forestry.core.utils.Translator;
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
		drawSprite(TextureManager.getInstance().getDefault("misc/energy"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		int xHeader = x + 22;
		int xBody = x + 12;

		EnergyManager energyManager = tile.getEnergyManager();

		drawHeader(Translator.translateToLocal("for.gui.energy"), xHeader, y + 8);

		drawSubheader(Translator.translateToLocal("for.gui.stored") + ':', xBody, y + 20);
		drawText(energyManager.getEnergyStored() + " RF", xBody, y + 32);

		drawSubheader(Translator.translateToLocal("for.gui.maxenergy") + ':', xBody, y + 44);
		drawText(energyManager.getMaxEnergyStored() + " RF", xBody, y + 56);

		drawSubheader(Translator.translateToLocal("for.gui.maxenergyreceive") + ':', xBody, y + 68);
		drawText(energyManager.getMaxEnergyReceived() + " RF", xBody, y + 80);
	}

	@Override
	public String getTooltip() {
		return tile.getEnergyManager().getEnergyStored() + " RF";
	}

}
