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
import forestry.energy.tiles.TileEnginePeat;

public class GuiEnginePeat extends GuiEngine<ContainerEnginePeat, TileEnginePeat> {

	public GuiEnginePeat(InventoryPlayer inventory, TileEnginePeat tile) {
		super(Constants.TEXTURE_PATH_GUI + "/peatengine.png", new ContainerEnginePeat(inventory, tile), tile);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		TileEnginePeat engine = inventory;
		int progress;
		if (engine.isBurning()) {
			progress = engine.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(guiLeft + 45, guiTop + 27 + 12 - progress, 176, 12 - progress, 14, progress + 2);
		}
	}
}
