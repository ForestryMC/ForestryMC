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

import forestry.apiculture.multiblock.IAlvearyControllerInternal;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.render.EnumTankLevel;

public class GuiAlveary extends GuiForestryTitled<ContainerAlveary, TileAlveary> {

	public GuiAlveary(InventoryPlayer inventory, TileAlveary tile) {
		super(Constants.TEXTURE_PATH_GUI + "/alveary.png", new ContainerAlveary(inventory, tile), tile);

		ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		IAlvearyControllerInternal alvearyController = inventory.getMultiblockLogic().getController();
		drawHealthMeter(guiLeft + 20, guiTop + 37, alvearyController.getHealthScaled(46), EnumTankLevel.rateTankLevel(alvearyController.getHealthScaled(100)));
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 0;

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}
}
