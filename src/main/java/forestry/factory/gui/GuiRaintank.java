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

import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.gadgets.MachineRaintank;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiRaintank extends GuiForestryTitled<MachineRaintank> {

	public GuiRaintank(InventoryPlayer inventory, MachineRaintank tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/raintank.png", new ContainerRaintank(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 53, 17, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		MachineRaintank machine = tile;

		if (machine.isFilling()) {
			int progress = machine.getFillProgressScaled(24);
			drawTexturedModalRect(guiLeft + 80, guiTop + 39, 176, 74, 24 - progress, 16);
		}

	}
}
