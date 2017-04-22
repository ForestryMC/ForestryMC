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
package forestry.core.gui;

import forestry.core.config.Constants;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.render.EnumTankLevel;
import forestry.core.tiles.TileAnalyzer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiAnalyzer extends GuiForestryTitled<ContainerAnalyzer> {
	private final TileAnalyzer tile;

	public GuiAnalyzer(InventoryPlayer inventory, TileAnalyzer tile) {
		super(Constants.TEXTURE_PATH_GUI + "/alyzer.png", new ContainerAnalyzer(inventory, tile), tile);
		this.tile = tile;
		this.ySize = 176;
		this.widgetManager.add(new TankWidget(this.widgetManager, 95, 24, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		drawAnalyzeMeter(guiLeft + 64, guiTop + 30, tile.getProgressScaled(46), EnumTankLevel.rateTankLevel(tile.getProgressScaled(100)));
	}

	private void drawAnalyzeMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 60;

		drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("analyzer");
	}
}
