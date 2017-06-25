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

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.core.CamouflageManager;
import forestry.core.climate.ClimateInfo;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.owner.IOwnedTile;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouse;

public class GuiGreenhouse extends GuiForestryTitled<ContainerGreenhouse> {

	private final IGreenhouseControllerInternal controller;
	private final IOwnedTile tile;
	private ClimateTextFields fields;

	public GuiGreenhouse(EntityPlayer player, TileGreenhouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse.png", new ContainerGreenhouse(player.inventory, tile), tile);
		this.controller = tile.getMultiblockLogic().getController();
		this.tile = tile;

		//Add the water tank
		widgetManager.add(new TankWidget(widgetManager, 152, 16, 0).setOverlayOrigin(176, 0));
		//Add the multiblock camouflage tabs
		WidgetCamouflageTab previous;
		int x = 3;
		widgetManager.add(previous = new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, controller, tile, CamouflageManager.BLOCK));
		x+=50 + (previous.getHandlerSlot()  != null ? 20 : 0);
		widgetManager.add(previous = new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, controller, tile, CamouflageManager.GLASS));
		x+=50 + (previous.getHandlerSlot()  != null ? 20 : 0);
		widgetManager.add(new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, controller, tile, CamouflageManager.DOOR));
		
		if(hasFields()){
			widgetManager.add(new WidgetClimatePillar(widgetManager, guiLeft- 23, guiTop + 5));
		}
	}
	
	public boolean hasFields(){
		return controller != null && controller.getControlClimate() != ClimateInfo.MAX;
	}

	@Override
	public void initGui() {
		super.initGui();
		
		if(hasFields()){
			fields = new ClimateTextFields(controller, fontRenderer, guiLeft, guiTop);
		}else{
			fields = null;
		}
	}
	
	public ClimateTextFields getFields() {
		return fields;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (fields == null || !fields.keyTyped(typedChar, keyCode)) {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (fields != null) {
			fields.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		if (fields != null) {
			fields.draw(this, mouseX, mouseY);
		}
	}
	
	TextLayoutHelper getTextLayout() {
		return textLayout;
	}
	
	@Override
	protected void addLedgers() {
		addErrorLedger(controller);
		ledgerManager.add(new GreenhouseEnergyLedger(ledgerManager, controller));
		addClimateLedger(controller);
		addHintLedger("greenhouse");
		addOwnerLedger(tile);
	}
}
