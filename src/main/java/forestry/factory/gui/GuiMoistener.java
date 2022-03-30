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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileMoistener;

public class GuiMoistener extends GuiForestryTitled<ContainerMoistener> {
	private final TileMoistener tile;

	public GuiMoistener(ContainerMoistener container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/moistener.png", container, inventory, title);
		this.tile = container.getTile();
		widgetManager.add(new TankWidget(this.widgetManager, 16, 16, 0));
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		// Mycelium production progress
		if (tile.isProducing()) {
			int i1 = tile.getProductionProgressScaled(16);
			blit(transform, leftPos + 124, topPos + 36, 176, 74, 16 - i1, 16);
		}

		// Resource consumption progress
		if (tile.isWorking()) {
			int i1 = tile.getConsumptionProgressScaled(54);
			blit(transform, leftPos + 93, topPos + 18 + i1, 176, 92 + i1, 29, 54 - i1);
		}
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("moistener");
	}
}
