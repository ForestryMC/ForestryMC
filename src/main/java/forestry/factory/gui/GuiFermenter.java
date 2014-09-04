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

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineFermenter;

public class GuiFermenter extends GuiForestry<MachineFermenter> {

	public GuiFermenter(InventoryPlayer inventory, MachineFermenter tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/fermenter.png", new ContainerFermenter(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 35, 19, 0));
		widgetManager.add(new TankWidget(this.widgetManager, 125, 19, 1));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localizeTile(tile.getInventoryName());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		MachineFermenter machine = tile;

		// Fuel remaining
		int fuelRemain = machine.getBurnTimeRemainingScaled(16);
		if (fuelRemain > 0)
			drawTexturedModalRect(guiLeft + 98, guiTop + 46 + 17 - fuelRemain, 176, 78 + 17 - fuelRemain, 4, fuelRemain);

		// Raw bio mush remaining
		int bioRemain = machine.getFermentationProgressScaled(16);
		if (bioRemain > 0)
			drawTexturedModalRect(guiLeft + 74, guiTop + 32 + 17 - bioRemain, 176, 60 + 17 - bioRemain, 4, bioRemain);
	}

}
