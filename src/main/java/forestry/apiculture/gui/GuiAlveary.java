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

import forestry.apiculture.multiblock.IAlvearyController;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.OwnerLedger;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.Utils;

public class GuiAlveary extends GuiForestryTitled<ContainerAlveary, TileAlvearyPlain> {

	public GuiAlveary(InventoryPlayer inventory, TileAlvearyPlain tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/alveary.png", new ContainerAlveary(inventory, tile), tile);

		ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		IAlvearyController alvearyController = inventory.getAlvearyController();
		drawHealthMeter(guiLeft + 20, guiTop + 37, alvearyController.getHealthScaled(46), Utils.rateTankLevel(alvearyController.getHealthScaled(100)));
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

	/* LEDGERS */
	protected void initLedgers() {
		super.initLedgers();

		IAlvearyController alvearyController = inventory.getAlvearyController();
		ledgerManager.add(new OwnerLedger(ledgerManager, alvearyController));
	}

}
