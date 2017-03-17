package forestry.greenhouse.gui;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;

import forestry.api.climate.IClimateControlProvider;
import forestry.api.climate.IClimateInfo;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.climate.ClimateInfo;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.network.packets.PacketUpdateClimateControl;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class ClimateTextFields {

	private final GuiTextField humidityField;
	private final GuiTextField temperatureField;
	private final IClimateControlProvider provider;
	
	public ClimateTextFields(IGreenhouseController controller, FontRenderer fontRenderer, int guiLeft, int guiTop) {
		this.provider = controller;
		IClimateInfo info = controller.getControlClimate();
		
		temperatureField = new GuiTextField(0, fontRenderer, guiLeft + 64, guiTop + 31, 50, 10);
		temperatureField.setValidator(NUMBER_FILTER);
		temperatureField.setEnableBackgroundDrawing(false);
		temperatureField.setText(Float.toString(info.getTemperature()));
		
		humidityField = new GuiTextField(1, fontRenderer, guiLeft + 64, guiTop + 61, 50, 10);
		humidityField.setValidator(NUMBER_FILTER);
		humidityField.setEnableBackgroundDrawing(false);
		humidityField.setText(Float.toString(info.getHumidity()));

	}
	
	protected boolean keyTyped(char typedChar, int keyCode) {
		return humidityField.textboxKeyTyped(typedChar, keyCode) || temperatureField.textboxKeyTyped(typedChar, keyCode);
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton){
		boolean temperatureWasFocused = temperatureField.isFocused();
		boolean humidityWasFocused = humidityField.isFocused();
		
		temperatureField.mouseClicked(mouseX, mouseY, mouseButton);
		humidityField.mouseClicked(mouseX, mouseY, mouseButton);
		
		if (temperatureWasFocused && !temperatureField.isFocused() || humidityWasFocused && !humidityField.isFocused()) {
			float temp = parseField(temperatureField);
			float hum = parseField(humidityField);
			setClimate(provider, temp, hum);
		}
		NetworkUtil.sendToServer(new PacketUpdateClimateControl(provider));
	}
	
	public void draw(GuiGreenhouse gui, int guiLeft, int guiTop){
		TextLayoutHelper textLayout = gui.getTextLayout();
		
		gui.drawTexturedModalRect(guiLeft + 62, guiTop + 29, 204, 115, 52, 12);
		gui.drawTexturedModalRect(guiLeft + 62, guiTop + 59, 204, 127, 52, 12);
		
		textLayout.line = 20;
		textLayout.drawCenteredLine(Translator.translateToLocal("for.gui.temperature"), 0, ColourProperties.INSTANCE.get("gui.greenhouse.temperature.text"));
		temperatureField.drawTextBox();
		
		textLayout.line = 50;
		textLayout.drawCenteredLine(Translator.translateToLocal("for.gui.humidity"), 0, ColourProperties.INSTANCE.get("gui.greenhouse.humidity.text"));
		humidityField.drawTextBox();
	}
	
	public void setClimate(float temp){
		IClimateInfo info = provider.getControlClimate();
		setClimate(provider, temp, info.getHumidity());
	}
	
	public void setClimate(IClimateControlProvider provider, float temp, float hum){
		temperatureField.setText(Float.toString(temp));
		humidityField.setText(Float.toString(hum));
		provider.setControlClimate(new ClimateInfo(temp, hum));
	}
	
	private static float parseField(GuiTextField field) {
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
	
	protected static final Predicate<String> NUMBER_FILTER = text -> {
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
	
}
