/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
