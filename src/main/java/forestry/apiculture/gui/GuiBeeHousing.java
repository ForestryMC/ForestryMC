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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.tiles.TileAbstractBeeHousing;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeehouse;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.render.EnumTankLevel;

public class GuiBeeHousing extends GuiForestryTitled<ContainerBeeHousing, TileAbstractBeeHousing> {

	public GuiBeeHousing(InventoryPlayer inventory, TileApiary tile) {
		super(Constants.TEXTURE_PATH_GUI + "/apiary.png", new ContainerBeeHousing(inventory, tile, true), tile);

		ySize = 190;
	}

	public GuiBeeHousing(InventoryPlayer inventory, TileBeehouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/alveary.png", new ContainerBeeHousing(inventory, tile, false), tile);

		ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		drawHealthMeter(guiLeft + 20, guiTop + 37, inventory.getHealthScaled(46), EnumTankLevel.rateTankLevel(inventory.getHealthScaled(100)));
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 0;

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

}
