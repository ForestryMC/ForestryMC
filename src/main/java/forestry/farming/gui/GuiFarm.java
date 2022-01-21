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
package forestry.farming.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.farming.FarmDirection;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.farming.multiblock.IFarmControllerInternal;
import forestry.farming.tiles.TileFarm;

public class GuiFarm extends GuiForestryTitled<ContainerFarm> {
	private final TileFarm tile;

	public GuiFarm(ContainerFarm container, Inventory inv, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/mfarm.png", container, inv, title);
		this.tile = container.getTile();

		widgetManager.add(new TankWidget(widgetManager, 15, 19, 0).setOverlayOrigin(216, 18));

		widgetManager.add(new SocketWidget(widgetManager, 69, 40, tile, 0));

		IFarmControllerInternal farmController = tile.getMultiblockLogic().getController();

		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 69, 22, FarmDirection.NORTH));
		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 69, 58, FarmDirection.SOUTH));
		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 51, 40, FarmDirection.WEST));
		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 87, 40, FarmDirection.EAST));

		this.imageWidth = 216;
		this.imageHeight = 220;
	}

	@Override
	protected void addLedgers() {
		IFarmControllerInternal farmController = tile.getMultiblockLogic().getController();

		addErrorLedger(farmController);
		addClimateLedger(farmController);
		ledgerManager.add(new FarmLedger(ledgerManager, farmController.getFarmLedgerDelegate()));
		addHintLedger("farm");
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		// Fuel remaining
		int fertilizerRemain = tile.getMultiblockLogic().getController().getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			blit(transform, leftPos + 81, topPos + 94 + 17 - fertilizerRemain, imageWidth, 17 - fertilizerRemain, 4, fertilizerRemain);
		}
	}
}
