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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineSqueezer;

public class GuiSqueezer extends GuiForestry<MachineSqueezer> {

	public GuiSqueezer(InventoryPlayer inventory, MachineSqueezer tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/squeezer.png", new ContainerSqueezer(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 85, 15, 0));
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
		MachineSqueezer machine = tile;

		int progress = machine.getProgressScaled(43);
		drawTexturedModalRect(guiLeft + 75, guiTop + 20, 176, 60, 43 - progress, 18);
	}

}
