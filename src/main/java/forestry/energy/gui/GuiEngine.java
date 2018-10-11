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
package forestry.energy.gui;

import net.minecraft.inventory.Container;

import forestry.core.config.Config;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.render.TextureManagerForestry;
import forestry.core.tiles.TileEngine;
import forestry.core.utils.Translator;

public abstract class GuiEngine<C extends Container, I extends TileEngine> extends GuiForestryTitled<C> {
	protected final I tile;

	protected GuiEngine(String texture, C container, I tile) {
		super(texture, container, tile);
		this.tile = tile;
	}

	protected class EngineLedger extends Ledger {

		public EngineLedger() {
			super(ledgerManager, "power");
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

			drawHeader(Translator.translateToLocal("for.gui.energy"), x + 22, y + 8);

			drawSubheader(Translator.translateToLocal("for.gui.currentOutput") + ':', x + 22, y + 20);
			drawText(Config.energyDisplayMode.formatRate(tile.getCurrentOutput()), x + 22, y + 32);

			drawSubheader(Translator.translateToLocal("for.gui.stored") + ':', x + 22, y + 44);
			drawText(Config.energyDisplayMode.formatEnergyValue(tile.getEnergyManager().getEnergyStored()), x + 22, y + 56);

			drawSubheader(Translator.translateToLocal("for.gui.heat") + ':', x + 22, y + 68);
			drawText((double) tile.getHeat() / (double) 10 + 20.0 + " C", x + 22, y + 80);
		}

		@Override
		public String getTooltip() {
			return Config.energyDisplayMode.formatRate(tile.getCurrentOutput());
		}
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger(tile.getHintKey());
		ledgerManager.add(new EngineLedger());
	}
}
