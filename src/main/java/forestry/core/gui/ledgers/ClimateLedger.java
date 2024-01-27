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

import forestry.api.climate.IClimatised;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

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
	public void draw(PoseStack transform, int y, int x) {

		EnumTemperature temperature = tile.getTemperature();

		// Draw background
		drawBackground(transform, y, x);

		// Draw icon
		drawSprite(transform, temperature.getSprite(), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		drawHeader(transform, Translator.translateToLocal("for.gui.climate"), x + 22, y + 8);

		//TODO textcomponent
		drawSubheader(transform, Translator.translateToLocal("for.gui.temperature") + ':', x + 22, y + 20);
		drawText(transform, AlleleManager.climateHelper.toDisplay(temperature).getString() + ' ' + StringUtil.floatAsPercent(tile.getExactTemperature()), x + 22, y + 32);

		drawSubheader(transform, Translator.translateToLocal("for.gui.humidity") + ':', x + 22, y + 44);
		drawText(transform, AlleleManager.climateHelper.toDisplay(tile.getHumidity()).getString() + ' ' + StringUtil.floatAsPercent(tile.getExactHumidity()), x + 22, y + 56);
	}

	@Override
	public Component getTooltip() {
		return Component.literal("T: ")
			.append(AlleleManager.climateHelper.toDisplay(tile.getTemperature()))
			.append(Component.literal(" / H: "))
			.append(AlleleManager.climateHelper.toDisplay(tile.getHumidity()));
	}

}
