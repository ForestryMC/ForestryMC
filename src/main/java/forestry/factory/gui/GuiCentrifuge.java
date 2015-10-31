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
import forestry.core.gui.widgets.SocketWidget;
import forestry.factory.tiles.TileCentrifuge;

public class GuiCentrifuge extends GuiForestryTitled<ContainerCentrifuge, TileCentrifuge> {

	public GuiCentrifuge(InventoryPlayer inventory, TileCentrifuge tile) {
		super(Constants.TEXTURE_PATH_GUI + "/centrifugesocket.png", new ContainerCentrifuge(inventory, tile), tile);
		widgetManager.add(new SocketWidget(this.widgetManager, 71, 37, tile, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int progress = inventory.getProgressScaled(16);
		drawTexturedModalRect(guiLeft + 58, guiTop + 36 + 17 - progress, 176, 17 - progress, 4, progress);
	}
}
