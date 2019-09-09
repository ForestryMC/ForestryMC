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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.core.config.Constants;
import forestry.energy.tiles.TileEnginePeat;

public class GuiEnginePeat extends GuiEngine<ContainerEnginePeat, TileEnginePeat> {

	public GuiEnginePeat(ContainerEnginePeat container, PlayerInventory inventory, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/peatengine.png", container, inventory, container.getTile());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		if (tile.isBurning()) {
			int progress = tile.getBurnTimeRemainingScaled(12);
			blit(guiLeft + 45, guiTop + 27 + 12 - progress, 176, 12 - progress, 14, progress + 2);
		}
	}
}
