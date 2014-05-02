/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.utils.StringUtil;

public class GuiAlvearyHygroregulator extends GuiForestry<TileAlvearyHygroregulator> {

	public GuiAlvearyHygroregulator(InventoryPlayer inventory, TileAlvearyHygroregulator tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/hygroregulator.png", new ContainerAlvearyHygroregulator(inventory, tile));

		widgetManager.add(new TankWidget(this.widgetManager, 104, 17, 0));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.alveary.5");
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

}
