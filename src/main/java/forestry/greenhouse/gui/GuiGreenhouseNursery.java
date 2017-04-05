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
package forestry.greenhouse.gui;

import forestry.api.core.CamouflageManager;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.render.EnumTankLevel;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouseNursery;
import net.minecraft.entity.player.EntityPlayer;

public class GuiGreenhouseNursery extends GuiForestryTitled<ContainerGreenhouseNursery> {
	private final TileGreenhouseNursery tile;

	public GuiGreenhouseNursery(EntityPlayer player, TileGreenhouseNursery tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse_nursery.png", new ContainerGreenhouseNursery(player.inventory, tile), tile);

		this.tile = tile;
		this.ySize = 176;
		
		IGreenhouseControllerInternal controller = tile.getMultiblockLogic().getController();
		WidgetCamouflageTab previous;
		int x = 3;
		widgetManager.add(previous = new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, controller, tile, CamouflageManager.BLOCK));
		x+=50 + (previous.getHandlerSlot()  != null ? 20 : 0);
		widgetManager.add(previous = new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, controller, tile, CamouflageManager.GLASS));
		x+=50 + (previous.getHandlerSlot()  != null ? 20 : 0);
		widgetManager.add(new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, controller, tile, CamouflageManager.DOOR));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		drawMeter(guiLeft + 75, guiTop + 30, tile.getProgressScaled(46), EnumTankLevel.rateTankLevel(tile.getProgressScaled(100)));
	}

	private void drawMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);

		drawTexturedModalRect(x, y + 46 - height, i, 46 - height, 4, height);
	}

	@Override
	protected void addLedgers() {
		IGreenhouseControllerInternal controller = tile.getMultiblockLogic().getController();

		addErrorLedger(controller);
		addClimateLedger(controller);
		ledgerManager.add(new GreenhouseEnergyLedger(ledgerManager, controller));
		addHintLedger("greenhouse");
		addOwnerLedger(tile);
	}
}
