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

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileSqueezer;

public class GuiSqueezer extends GuiForestryTitled<ContainerSqueezer, TileSqueezer> {

	public GuiSqueezer(InventoryPlayer inventory, TileSqueezer tile) {
		super(Constants.TEXTURE_PATH_GUI + "/squeezersocket.png", new ContainerSqueezer(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 122, 18, 0));
		widgetManager.add(new SocketWidget(this.widgetManager, 75, 20, tile, 0));
	}

	@Override
	protected void drawWidgets() {
		int progress = inventory.getProgressScaled(43);
		drawTexturedModalRect(75, 41, 176, 60, progress, 18);

		super.drawWidgets();
	}

}
