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
import forestry.factory.multiblock.IDistillVatControllerInternal;
import forestry.factory.multiblock.TileDistillVat;

public class GuiDistillVat extends GuiForestryTitled<ContainerDistillVat, TileDistillVat> {

	public GuiDistillVat(InventoryPlayer inventory, TileDistillVat tile) {
		super(Constants.TEXTURE_PATH_GUI + "/still.png", new ContainerDistillVat(inventory, tile), tile);

		widgetManager.add(new TankWidget(this.widgetManager, 35, 15, 0));
		widgetManager.add(new TankWidget(this.widgetManager, 125, 15, 1));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		IDistillVatControllerInternal distillVatController = inventory.getMultiblockLogic().getController();

		drawTexturedModalRect(guiLeft + 81, guiTop + 57, 176, 60, 14, 14);

		if (distillVatController.getWorkCounter() > 0) {
			int massRemaining = distillVatController.getProgressScaled(16);
			drawTexturedModalRect(guiLeft + 84, guiTop + 17 + massRemaining, 176, 74 + massRemaining, 4, 17 - massRemaining);
		}
	}

}
