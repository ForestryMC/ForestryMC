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
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileBottler;

public class GuiBottler extends GuiForestryTitled<ContainerBottler, TileBottler> {

	private static final int PROGRESS_SIZE = 24;

	public GuiBottler(InventoryPlayer inventory, TileBottler processor) {
		super(Constants.TEXTURE_PATH_GUI + "/bottler.png", new ContainerBottler(inventory, processor), processor);
		widgetManager.add(new TankWidget(this.widgetManager, 53, 17, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		TileBottler bottler = inventory;

		int i1 = bottler.getProgressScaled(PROGRESS_SIZE);
		drawTexturedModalRect(guiLeft + 80, guiTop + 39, 176, 74, PROGRESS_SIZE - i1, 16);
	}
}
