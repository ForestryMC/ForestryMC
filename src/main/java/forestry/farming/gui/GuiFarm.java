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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.api.farming.FarmDirection;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.farming.multiblock.IFarmControllerInternal;
import forestry.farming.tiles.TileFarm;

public class GuiFarm extends GuiForestryTitled<ContainerFarm> {
	private final TileFarm tile;

	public GuiFarm(ContainerFarm container, PlayerInventory inv, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/mfarm.png", container, inv, container.getTile());
		this.tile = container.getTile();

		widgetManager.add(new TankWidget(widgetManager, 15, 19, 0).setOverlayOrigin(216, 18));

		widgetManager.add(new SocketWidget(widgetManager, 69, 40, tile, 0));

		IFarmControllerInternal farmController = tile.getMultiblockLogic().getController();

		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 69, 22, FarmDirection.NORTH));
		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 69, 58, FarmDirection.SOUTH));
		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 51, 40, FarmDirection.WEST));
		widgetManager.add(new FarmLogicSlot(farmController, widgetManager, 87, 40, FarmDirection.EAST));

		this.xSize = 216;
		this.ySize = 220;
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
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		// Fuel remaining
		int fertilizerRemain = tile.getMultiblockLogic().getController().getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			blit(guiLeft + 81, guiTop + 94 + 17 - fertilizerRemain, xSize, 17 - fertilizerRemain, 4, fertilizerRemain);
		}
	}
}
