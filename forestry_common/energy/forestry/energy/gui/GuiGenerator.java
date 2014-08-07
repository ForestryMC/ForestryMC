/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.MachineGenerator;

public class GuiGenerator extends GuiForestry<MachineGenerator> {

	public GuiGenerator(InventoryPlayer inventory, MachineGenerator tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/generator.png", new ContainerGenerator(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 49, 17, 0));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String title = StringUtil.localize("tile.for." + tile.getInventoryName());
		this.fontRendererObj.drawString(title, getCenteredOffset(title), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		MachineGenerator boiler = tile;

		int progress = boiler.getStoredScaled(49);
		if (progress > 0)
			drawTexturedModalRect(guiLeft + 108, guiTop + 38, 176, 91, progress, 18);
	}

}
