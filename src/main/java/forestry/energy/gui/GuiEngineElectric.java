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
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Constants;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.render.EnumTankLevel;
import forestry.energy.tiles.TileEngineElectric;

public class GuiEngineElectric extends GuiEngine<ContainerEngineElectric, TileEngineElectric> {

	public GuiEngineElectric(InventoryPlayer inventory, TileEngineElectric tile) {
		super(Constants.TEXTURE_PATH_GUI + "/electricalengine.png", new ContainerEngineElectric(inventory, tile), tile);
		widgetManager.add(new SocketWidget(this.widgetManager, 30, 40, tile, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		TileEngineElectric engine = inventory;
		int storageHeight = engine.getStorageScaled(46);
		int storageMaxHeight = engine.getStorageScaled(100);
		EnumTankLevel rated = EnumTankLevel.rateTankLevel(storageMaxHeight);

		drawHealthMeter(guiLeft + 74, guiTop + 25, storageHeight, rated);
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 0;

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

}
