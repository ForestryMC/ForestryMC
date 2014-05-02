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
import forestry.core.gui.widgets.ReservoirWidget;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineFabricator;

public class GuiFabricator extends GuiForestry<MachineFabricator> {

	public GuiFabricator(InventoryPlayer player, MachineFabricator tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/fabricator.png", new ContainerFabricator(player, tile), tile);
		this.ySize = 211;
		widgetManager.add(new ReservoirWidget(this.widgetManager, 26, 48, 0));
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

		MachineFabricator fabricator = tile;
		int heatScaled = fabricator.getHeatScaled(52);
		if (heatScaled > 0)
			drawTexturedModalRect(guiLeft + 55, guiTop + 17 + 52 - heatScaled, 192, 0 + 52 - heatScaled, 4, heatScaled);

		int meltingPointScaled = fabricator.getMeltingPointScaled(52);
		if (meltingPointScaled > 0)
			drawTexturedModalRect(guiLeft + 52, guiTop + 15 + 52 - meltingPointScaled, 196, 0, 10, 5);
	}

}
