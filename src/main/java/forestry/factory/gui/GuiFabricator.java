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
import forestry.core.gui.widgets.ReservoirWidget;
import forestry.factory.tiles.TileFabricator;

public class GuiFabricator extends GuiForestryTitled<ContainerFabricator> {
	private final TileFabricator tile;

	public GuiFabricator(ContainerFabricator container, Inventory player, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/fabricator.png", container, player, title);

		this.tile = container.getTile();
		this.imageHeight = 211;
		this.widgetManager.add(new ReservoirWidget(this.widgetManager, 26, 48, 0));
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		int heatScaled = tile.getHeatScaled(52);
		if (heatScaled > 0) {
			blit(transform, leftPos + 55, topPos + 17 + 52 - heatScaled, 192, 52 - heatScaled, 4, heatScaled);
		}

		int meltingPointScaled = tile.getMeltingPointScaled(52);
		if (meltingPointScaled > 0) {
			blit(transform, leftPos + 52, topPos + 15 + 52 - meltingPointScaled, 196, 0, 10, 5);
		}
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addPowerLedger(tile.getEnergyManager());
		addHintLedger("fabricator");
	}
}
