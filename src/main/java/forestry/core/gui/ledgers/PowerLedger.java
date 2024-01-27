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

import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

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
	public void draw(PoseStack transform, int y, int x) {
		// Draw background
		drawBackground(transform, y, x);

		// Draw icon
		drawSprite(transform, TextureManagerForestry.getInstance().getDefault("misc/energy"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		int xHeader = x + 22;
		int xBody = x + 12;

		drawHeader(transform, Translator.translateToLocal("for.gui.energy"), xHeader, y + 8);

		drawSubheader(transform, Translator.translateToLocal("for.gui.stored") + ':', xBody, y + 20);
		drawText(transform, Config.energyDisplayMode.formatEnergyValue(energyManager.getEnergyStored()), xBody, y + 32);

		drawSubheader(transform, Translator.translateToLocal("for.gui.maxenergy") + ':', xBody, y + 44);
		drawText(transform, Config.energyDisplayMode.formatEnergyValue(energyManager.getMaxEnergyStored()), xBody, y + 56);

		drawSubheader(transform, Translator.translateToLocal("for.gui.maxenergyreceive") + ':', xBody, y + 68);
		drawText(transform, Config.energyDisplayMode.formatEnergyValue(energyManager.getMaxEnergyReceived()), xBody, y + 80);
	}

	@Override
	public Component getTooltip() {
		return Component.literal(Config.energyDisplayMode.formatEnergyValue(energyManager.getEnergyStored()));
	}

}
