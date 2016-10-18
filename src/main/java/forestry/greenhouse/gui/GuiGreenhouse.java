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

import javax.annotation.Nullable;
import java.io.IOException;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimateControlProvider;
import forestry.api.core.CamouflageManager;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.WidgetCamouflageSlot;
import forestry.core.network.packets.PacketUpdateClimateControl;
import forestry.core.proxy.Proxies;
import forestry.core.render.ColourProperties;
import forestry.core.render.TextureManager;
import forestry.core.utils.Translator;
import forestry.greenhouse.multiblock.DefaultClimateControl;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouse;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

public class GuiGreenhouse extends GuiForestryTitled<ContainerGreenhouse, TileGreenhouse> {

    private static final Predicate<String> numberFilter = new Predicate<String>(){
        @Override
		public boolean apply(@Nullable String text){
        	if(!text.isEmpty() && text.length() > 1){
        		if(!text.contains(".")){
        			return false;
        		}
        	}
        	if(text.length() > 7){
        		return false;
        	}
            Float f = Floats.tryParse(text);
            return text.isEmpty() || f != null && Floats.isFinite(f.floatValue()) && f.floatValue() >= 0.0F;
        }
    };
	
	private GuiTextField humidityField;
	private GuiTextField temperatureField;
	private boolean fieldsEnabeled;
	
	public GuiGreenhouse(EntityPlayer player, TileGreenhouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse.png", new ContainerGreenhouse(player.inventory, tile), tile);

		//Add the water tank
		widgetManager.add(new TankWidget(widgetManager, 152, 16, 0).setOverlayOrigin(176, 0));
		
		//Add the multiblock camouflage slots
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 16, inventory.getMultiblockLogic().getController(), CamouflageManager.DEFAULT));
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 37, inventory.getMultiblockLogic().getController(), CamouflageManager.GLASS));
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 58, inventory.getMultiblockLogic().getController(), CamouflageManager.DOOR));
		
		//Add the tile camouflage slots
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 35, 37, inventory, tile.getCamouflageType()));
		fieldsEnabeled = true;
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		temperatureField = new GuiTextField(0, fontRendererObj, guiLeft + 63, guiTop + 30, 50, 10);
		humidityField = new GuiTextField(1, fontRendererObj, guiLeft + 63, guiTop + 60, 50, 10);
		
		temperatureField.setValidator(numberFilter);
		humidityField.setValidator(numberFilter);
		
		IGreenhouseController controller = inventory.getMultiblockLogic().getController();
		if(controller == null || controller.getClimateControl() == null || controller.getClimateControl() == DefaultClimateControl.instance){
			temperatureField.setEnabled(false);
			temperatureField.setVisible(false);
			humidityField.setEnabled(false);
			humidityField.setVisible(false);
			fieldsEnabeled = false;
		}else{
			IClimateControl control = controller.getClimateControl();
			temperatureField.setText(Float.toString(control.getControlTemperature()));
			humidityField.setText(Float.toString(control.getControlHumidity()));
		}
	}
	
    @Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
    	 if(fieldsEnabeled){
	        if(!humidityField.textboxKeyTyped(typedChar, keyCode) && !temperatureField.textboxKeyTyped(typedChar, keyCode)){
	        	super.keyTyped(typedChar, keyCode);
	        }
    	 }else{
    		 super.keyTyped(typedChar, keyCode);
    	 }
    }

    @Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(fieldsEnabeled){
	        boolean temperatureWasFocused = temperatureField.isFocused();
	        boolean  humidityWasFocused = humidityField.isFocused();
	        temperatureField.mouseClicked(mouseX, mouseY, mouseButton);
	        humidityField.mouseClicked(mouseX, mouseY, mouseButton);
	        IClimateControlProvider provider = inventory.getMultiblockLogic().getController();
	        IClimateControl control = provider.getClimateControl();
	        if(temperatureWasFocused && !temperatureField.isFocused()){
	        	float temp = parseField(temperatureField);
	        	temperatureField.setText(Float.toString(temp));
	            control.setControlTemperature(temp);
	        }else if(humidityWasFocused && !humidityField.isFocused()){
	        	float hum = parseField(humidityField);
	        	humidityField.setText(Float.toString(hum));
	        	control.setControlHumidity(parseField(humidityField));
	        }
	        Proxies.net.sendToServer(new PacketUpdateClimateControl(provider));
        }
    }
    
    private float parseField(GuiTextField field){
    	String text = field.getText();
    	if(text.isEmpty()){
    		return 2.0F;
    	}
        try{
        	float f = Float.parseFloat(text);
            if(f >= 2){
            	f = 2.0F;
            }
            return f;
        }catch (NumberFormatException var5){
        	return 2.0F;
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
    	super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
    	
    	if(temperatureField.getVisible()){
			textLayout.line = 20;
			textLayout.drawCenteredLine(Translator.translateToLocal("for.gui.temperature"), 0, ColourProperties.INSTANCE.get("gui.greenhouse.temperature.text"));
	        temperatureField.drawTextBox();
    	}
    	if(humidityField.getVisible()){
			textLayout.line = 50;
			textLayout.drawCenteredLine(Translator.translateToLocal("for.gui.humidity"), 0, ColourProperties.INSTANCE.get("gui.greenhouse.humidity.text"));
	    	
	        humidityField.drawTextBox();
    	}
		bindTexture(textureFile);
    }

	@Override
	protected void addLedgers() {
		IGreenhouseControllerInternal greenhouseController = inventory.getMultiblockLogic().getController();
		
		ledgerManager.add(new ClimateLedger(ledgerManager, greenhouseController));
		ledgerManager.add(new EnergyLedger());
		super.addLedgers();
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
			drawText(inventory.getMultiblockLogic().getController().getEnergyManager().getEnergyStored() + " RF", x + 22, y + 32);
		}

		@Override
		public String getTooltip() {
			return Translator.translateToLocal("for.gui.energy") + ": " + inventory.getMultiblockLogic().getController().getEnergyManager().getEnergyStored() + " RF/t";
		}
	}
}
