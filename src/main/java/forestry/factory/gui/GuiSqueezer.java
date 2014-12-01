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
import forestry.factory.gadgets.MachineSqueezer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSqueezer extends GuiForestryTitled<MachineSqueezer> {

	public GuiSqueezer(InventoryPlayer inventory, MachineSqueezer tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/squeezer.png", new ContainerSqueezer(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 85, 15, 0));
	}

	@Override
	protected void drawWidgets() {
		int progress = tile.getProgressScaled(43);
		drawTexturedModalRect(75, 20, 176, 60, 43 - progress, 18);

		super.drawWidgets();
	}

}
