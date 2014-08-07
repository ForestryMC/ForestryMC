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
