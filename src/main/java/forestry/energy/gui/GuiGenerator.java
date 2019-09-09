/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.energy.tiles.TileEuGenerator;

public class GuiGenerator extends GuiForestryTitled<ContainerGenerator> {

	private final TileEuGenerator tile;

	public GuiGenerator(ContainerGenerator container, PlayerInventory inventory, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/generator.png", container, inventory, container.getTile());
		widgetManager.add(new TankWidget(this.widgetManager, 49, 17, 0));
		this.tile = container.getTile();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int progress = tile.getStoredScaled(49);
		if (progress > 0) {
			blit(guiLeft + 108, guiTop + 38, 176, 91, progress, 18);
		}
	}

	@Override
	protected void addLedgers() {

	}

}
