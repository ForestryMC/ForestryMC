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
import forestry.factory.tiles.TileCarpenter;

public class GuiCarpenter extends GuiForestryTitled<ContainerCarpenter, TileCarpenter> {

	public GuiCarpenter(InventoryPlayer inventory, TileCarpenter tile) {
		super(Constants.TEXTURE_PATH_GUI + "/carpenter.png", new ContainerCarpenter(inventory, tile), tile);
		this.ySize = 218;

		widgetManager.add(new TankWidget(this.widgetManager, 150, 17, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		int progressScaled = inventory.getProgressScaled(16);
		drawTexturedModalRect(guiLeft + 98, guiTop + 51 + 16 - progressScaled, 176, 60 + 16 - progressScaled, 4, progressScaled);
	}

}
