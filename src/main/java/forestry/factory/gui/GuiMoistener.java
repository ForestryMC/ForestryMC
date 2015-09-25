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
import forestry.factory.tiles.TileMoistener;

public class GuiMoistener extends GuiForestryTitled<ContainerMoistener, TileMoistener> {

	public GuiMoistener(InventoryPlayer inventory, TileMoistener tile) {
		super(Constants.TEXTURE_PATH_GUI + "/moistener.png", new ContainerMoistener(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 16, 16, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		// Mycelium production progress
		if (inventory.isProducing()) {
			int i1 = inventory.getProductionProgressScaled(16);
			drawTexturedModalRect(guiLeft + 124, guiTop + 36, 176, 74, 16 - i1, 16);
		}

		// Resource consumption progress
		if (inventory.isWorking()) {
			int i1 = inventory.getConsumptionProgressScaled(54);
			drawTexturedModalRect(guiLeft + 93, guiTop + 18 + i1, 176, 92 + i1, 29, 54 - i1);
		}
	}

}
