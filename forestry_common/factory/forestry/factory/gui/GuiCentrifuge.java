/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineCentrifuge;

public class GuiCentrifuge extends GuiForestry<MachineCentrifuge> {

	public GuiCentrifuge(InventoryPlayer inventory, MachineCentrifuge tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/centrifuge.png", new ContainerCentrifuge(inventory, tile), tile);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.for." + tile.getInventoryName());
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		MachineCentrifuge machine = tile;

		int progress = 16 - machine.getProgressScaled(16);
		drawTexturedModalRect(guiLeft + 62, guiTop + 36 + 17 - progress, 176, 17 - progress, 4, progress);
	}
}
