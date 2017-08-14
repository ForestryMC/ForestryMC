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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileFermenter;

public class GuiFermenter extends GuiForestryTitled<ContainerFermenter> {
	private final TileFermenter tile;

	public GuiFermenter(InventoryPlayer inventory, TileFermenter tile) {
		super(Constants.TEXTURE_PATH_GUI + "/fermenter.png", new ContainerFermenter(inventory, tile), tile);
		this.tile = tile;
		widgetManager.add(new TankWidget(this.widgetManager, 35, 19, 0));
		widgetManager.add(new TankWidget(this.widgetManager, 125, 19, 1));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		// Fuel remaining
		int fuelRemain = tile.getBurnTimeRemainingScaled(16);
		if (fuelRemain > 0) {
			drawTexturedModalRect(guiLeft + 98, guiTop + 46 + 17 - fuelRemain, 176, 78 + 17 - fuelRemain, 4, fuelRemain);
		}

		// Raw bio mush remaining
		int bioRemain = tile.getFermentationProgressScaled(16);
		if (bioRemain > 0) {
			drawTexturedModalRect(guiLeft + 74, guiTop + 32 + 17 - bioRemain, 176, 60 + 17 - bioRemain, 4, bioRemain);
		}
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("fermenter");
		addPowerLedger(tile.getEnergyManager());
	}
}
