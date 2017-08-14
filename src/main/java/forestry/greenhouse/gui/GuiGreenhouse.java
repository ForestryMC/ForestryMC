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
import net.minecraft.util.math.BlockPos;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateData;
import forestry.api.climate.ImmutableClimateState;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.gui.widgets.WidgetCamouflageTab;
import forestry.greenhouse.gui.widgets.WidgetClimatePanel;
import forestry.greenhouse.gui.widgets.WidgetClimatePillar;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.network.packets.PacketSelectClimateTargeted;
import forestry.greenhouse.tiles.TileGreenhouse;

public class GuiGreenhouse extends GuiForestryTitled<ContainerGreenhouse> {

	//The space between the top of the gui and the inventory.
	private static final int GUI_HEIGHT = 119;

	private final IGreenhouseControllerInternal controller;
	private final TileGreenhouse tile;
	public WidgetClimatePanel temperaturePanel;
	public WidgetClimatePanel humidityPanel;
	public IClimateContainer container;

	public GuiGreenhouse(EntityPlayer player, TileGreenhouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse.png", new ContainerGreenhouse(player.inventory, tile), tile);
		this.controller = tile.getMultiblockLogic().getController();
		this.container = controller.getClimateContainer();
		this.tile = tile;
		this.xSize = 196;
		this.ySize = 202;

		IClimateData data = container.getData();

		//Add the camouflage tab
		widgetManager.add(new WidgetCamouflageTab(widgetManager, xSize / 2 - WidgetCamouflageTab.WIDTH / 2, -WidgetCamouflageTab.HEIGHT, controller, tile));
		widgetManager.add(new WidgetClimatePillar(widgetManager, -WidgetClimatePillar.WIDTH, GUI_HEIGHT / 2 - WidgetClimatePillar.HEIGHT / 2));
		widgetManager.add(temperaturePanel = new WidgetClimatePanel(widgetManager, this, 9, 18, ClimateType.TEMPERATURE, data));
		widgetManager.add(humidityPanel = new WidgetClimatePanel(widgetManager, this, 102, 18, ClimateType.HUMIDITY, data));
	}

	public void sendNetworkUpdate() {
		BlockPos pos = controller.getCoordinates();
		float temp = temperaturePanel.parseValue();
		float hum = humidityPanel.parseValue();
		setClimate(container, temp, hum);
		NetworkUtil.sendToServer(new PacketSelectClimateTargeted(pos, container.getTargetedState()));
	}

	public void setClimate(IClimateContainer container, float temp, float hum) {
		temperaturePanel.setValue(temp);
		humidityPanel.setValue(hum);
		container.setTargetedState(new ImmutableClimateState(temp, hum));
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!temperaturePanel.keyTyped(typedChar, keyCode) && !humidityPanel.keyTyped(typedChar, keyCode)) {
			super.keyTyped(typedChar, keyCode);
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
