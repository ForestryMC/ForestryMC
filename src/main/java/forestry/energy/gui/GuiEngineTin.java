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

public class GuiEngineTin extends GuiEngine<ContainerEngineTin, TileEngineElectric> {

	public GuiEngineTin(InventoryPlayer inventory, TileEngineElectric tile) {
		super(Constants.TEXTURE_PATH_GUI + "/electricalengine.png", new ContainerEngineTin(inventory, tile), tile);
		widgetManager.add(new SocketWidget(this.widgetManager, 30, 40, tile, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		TileEngineElectric engine = inventory;
		int storageHeight = engine.getStorageScaled(46);
		int storageMaxHeight = engine.getStorageScaled(100);
		EnumTankLevel rated = rateLevel(storageMaxHeight);

		drawHealthMeter(guiLeft + 74, guiTop + 25, storageHeight, rated);
	}

	private static EnumTankLevel rateLevel(int scaled) {

		if (scaled < 5) {
			return EnumTankLevel.EMPTY;
		} else if (scaled < 30) {
			return EnumTankLevel.LOW;
		} else if (scaled < 60) {
			return EnumTankLevel.MEDIUM;
		} else if (scaled < 90) {
			return EnumTankLevel.HIGH;
		} else {
			return EnumTankLevel.MAXIMUM;
		}
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176;
		int k = 0;
		switch (rated) {
			case EMPTY:
				break;
			case LOW:
				i += 4;
				break;
			case MEDIUM:
				i += 8;
				break;
			case HIGH:
				i += 12;
				break;
			case MAXIMUM:
				i += 16;
				break;
		}

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

}
