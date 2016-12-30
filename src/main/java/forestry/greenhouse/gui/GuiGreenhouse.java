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

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import forestry.api.climate.IClimateControlProvider;
import forestry.api.climate.IClimateInfo;
import forestry.api.core.CamouflageManager;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.climate.ClimateInfo;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.network.packets.PacketUpdateClimateControl;
import forestry.core.proxy.Proxies;
import forestry.core.render.ColourProperties;
import forestry.core.render.TextureManager;
import forestry.core.utils.Translator;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouse;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

public class GuiGreenhouse extends GuiForestryTitled<ContainerGreenhouse> {

	private static final Predicate<String> numberFilter = text -> {
		if (text == null) {
			return false;
		}
		if (!text.isEmpty() && text.length() > 1) {
			if (!text.contains(".")) {
				return false;
			}
		}
		if (text.length() > 7) {
			return false;
		}
		Float f = Floats.tryParse(text);
		return text.isEmpty() || f != null && Floats.isFinite(f) && f >= 0.0F;
	};

	private final TileGreenhouse tile;
	private GuiTextField humidityField;
	private GuiTextField temperatureField;
	private boolean fieldsEnabeled;

	public GuiGreenhouse(EntityPlayer player, TileGreenhouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse.png", new ContainerGreenhouse(player.inventory, tile), tile);
		this.tile = tile;

		//Add the water tank
		widgetManager.add(new TankWidget(widgetManager, 152, 16, 0).setOverlayOrigin(176, 0));
		//Add the multiblock camouflage tabs
		WidgetCamouflageTab previous;
		int x = 3;
		widgetManager.add(previous = new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, tile.getMultiblockLogic().getController(), tile, CamouflageManager.BLOCK));
		x+=50 + (previous.getHandlerSlot()  != null ? 20 : 0);
		widgetManager.add(previous = new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, tile.getMultiblockLogic().getController(), tile, CamouflageManager.GLASS));
		x+=50 + (previous.getHandlerSlot()  != null ? 20 : 0);
		widgetManager.add(new WidgetCamouflageTab(widgetManager, guiLeft + x, guiTop - 25, tile.getMultiblockLogic().getController(), tile, CamouflageManager.DOOR));
		
		widgetManager.add(new WidgetClimatePillar(widgetManager, guiLeft- 23, guiTop + 5));
		
		fieldsEnabeled = true;
	}

	@Override
	public void initGui() {
		super.initGui();

		temperatureField = new GuiTextField(0, fontRendererObj, guiLeft + 64, guiTop + 31, 50, 10);
		humidityField = new GuiTextField(1, fontRendererObj, guiLeft + 64, guiTop + 61, 50, 10);

		temperatureField.setValidator(numberFilter);
		humidityField.setValidator(numberFilter);
		
		temperatureField.setEnableBackgroundDrawing(false);
		humidityField.setEnableBackgroundDrawing(false);

		IGreenhouseController controller = tile.getMultiblockLogic().getController();
		if (controller == null || controller.getControlClimate() == ClimateInfo.MAX) {
			temperatureField.setEnabled(false);
			temperatureField.setVisible(false);
			humidityField.setEnabled(false);
			humidityField.setVisible(false);
			fieldsEnabeled = false;
		} else {
			IClimateInfo info = controller.getControlClimate();
			temperatureField.setText(Float.toString(info.getTemperature()));
			humidityField.setText(Float.toString(info.getHumidity()));
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (fieldsEnabeled) {
			if (!humidityField.textboxKeyTyped(typedChar, keyCode) && !temperatureField.textboxKeyTyped(typedChar, keyCode)) {
				super.keyTyped(typedChar, keyCode);
			}
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (fieldsEnabeled) {
			boolean temperatureWasFocused = temperatureField.isFocused();
			boolean humidityWasFocused = humidityField.isFocused();
			temperatureField.mouseClicked(mouseX, mouseY, mouseButton);
			humidityField.mouseClicked(mouseX, mouseY, mouseButton);
			IClimateControlProvider provider = tile.getMultiblockLogic().getController();
			if (temperatureWasFocused && !temperatureField.isFocused() || humidityWasFocused && !humidityField.isFocused()) {
				float temp = parseField(temperatureField);
				float hum = parseField(humidityField);
				setClimate(provider, temp, hum);
			}
			Proxies.net.sendToServer(new PacketUpdateClimateControl(provider));
		}
	}
	
	public void setClimate(float temp){
		setClimate(tile.getMultiblockLogic().getController(), temp);
	}
	
	public void setClimate(IClimateControlProvider provider, float temp){
		IClimateInfo info = provider.getControlClimate();
		setClimate(provider, temp, info.getHumidity());
	}
	
	public void setClimate(IClimateControlProvider provider, float temp, float hum){
		temperatureField.setText(Float.toString(temp));
		humidityField.setText(Float.toString(hum));
		provider.setControlClimate(new ClimateInfo(temp, hum));
	}

	private float parseField(GuiTextField field) {
		String text = field.getText();
		if (text.isEmpty()) {
			return 2.0F;
		}
		try {
			float f = Float.parseFloat(text);
			if (f >= 2) {
				f = 2.0F;
			}
			return f;
		} catch (NumberFormatException var5) {
			return 2.0F;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		
		if (fieldsEnabeled) {		
			drawTexturedModalRect(guiLeft + 62, guiTop + 29, 204, 115, 52, 12);
			drawTexturedModalRect(guiLeft + 62, guiTop + 59, 204, 127, 52, 12);
		
			textLayout.line = 20;
			textLayout.drawCenteredLine(Translator.translateToLocal("for.gui.temperature"), 0, ColourProperties.INSTANCE.get("gui.greenhouse.temperature.text"));
			temperatureField.drawTextBox();
			textLayout.line = 50;
			textLayout.drawCenteredLine(Translator.translateToLocal("for.gui.humidity"), 0, ColourProperties.INSTANCE.get("gui.greenhouse.humidity.text"));

			humidityField.drawTextBox();
		}
	}

	@Override
	protected void addLedgers() {
		IGreenhouseControllerInternal greenhouseController = tile.getMultiblockLogic().getController();

		addErrorLedger(greenhouseController);
		ledgerManager.add(new EnergyLedger());
		addClimateLedger(greenhouseController);
		addHintLedger("greenhouse");
		addOwnerLedger(tile);
	}

	protected class EnergyLedger extends Ledger {

		public EnergyLedger() {
			super(ledgerManager, "power");
			maxHeight = 48;
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawSprite(TextureManager.getInstance().getDefault("misc/energy"), x + 3, y + 4);

			if (!isFullyOpened()) {
				return;
			}

			drawHeader(Translator.translateToLocal("for.gui.energy"), x + 22, y + 8);

			drawSubheader(Translator.translateToLocal("for.gui.stored") + ':', x + 22, y + 20);
			drawText(tile.getMultiblockLogic().getController().getEnergyManager().getEnergyStored() + " RF", x + 22, y + 32);
		}

		@Override
		public String getTooltip() {
			return Translator.translateToLocal("for.gui.energy") + ": " + tile.getMultiblockLogic().getController().getEnergyManager().getEnergyStored() + " RF/t";
		}
	}
}
