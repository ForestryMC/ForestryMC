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

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import forestry.core.config.Config;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.Translator;
import forestry.energy.EnergyManager;

public class PowerLedger extends Ledger {
	private final EnergyManager energyManager;

	public PowerLedger(LedgerManager manager, EnergyManager energyManager) {
		super(manager, "power");
		this.energyManager = energyManager;
		maxHeight = 94;
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawSprite(TextureManagerForestry.getInstance().getDefault("misc/energy"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		int xHeader = x + 22;
		int xBody = x + 12;

		drawHeader(Translator.translateToLocal("for.gui.energy"), xHeader, y + 8);

		drawSubheader(Translator.translateToLocal("for.gui.stored") + ':', xBody, y + 20);
		drawText(Config.energyDisplayMode.formatEnergyValue(energyManager.getEnergyStored()), xBody, y + 32);

		drawSubheader(Translator.translateToLocal("for.gui.maxenergy") + ':', xBody, y + 44);
		drawText(Config.energyDisplayMode.formatEnergyValue(energyManager.getMaxEnergyStored()), xBody, y + 56);

		drawSubheader(Translator.translateToLocal("for.gui.maxenergyreceive") + ':', xBody, y + 68);
		drawText(Config.energyDisplayMode.formatEnergyValue(energyManager.getMaxEnergyReceived()), xBody, y + 80);
	}

	@Override
	public ITextComponent getTooltip() {
		return new StringTextComponent(Config.energyDisplayMode.formatEnergyValue(energyManager.getEnergyStored()));
	}

}
