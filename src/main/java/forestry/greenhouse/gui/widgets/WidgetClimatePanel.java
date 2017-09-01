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
package forestry.greenhouse.gui.widgets;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.input.Keyboard;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.core.gui.tables.Table;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import forestry.greenhouse.api.climate.IClimateData;
import forestry.greenhouse.gui.GuiGreenhouse;

public class WidgetClimatePanel extends Widget {

	protected static final int TEXT_FIELD_LENGTH = 50;
	protected static final Predicate<String> NUMBER_FILTER = text -> {
		if (text == null) {
			return false;
		}
		if (text.isEmpty()) {
			return true;
		}
		if (text.length() > 1) {
			if (!text.contains(".")) {
				return false;
			}
		}
		if (text.length() > 7) {
			return false;
		}
		Float value = Floats.tryParse(text);
		return value != null && Floats.isFinite(value) && value >= 0.0F;
	};
	private final GuiGreenhouse gui;
	private final ClimateType type;
	private final Table table;
	private final GuiTextField textField;

	public WidgetClimatePanel(WidgetManager manager, GuiGreenhouse gui, int xPos, int yPos, ClimateType type) {
		super(manager, xPos, yPos);
		this.width = 85;
		this.height = 98;
		this.type = type;
		this.gui = gui;
		textField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, xPos + width / 2 - TEXT_FIELD_LENGTH / 2, yPos + 14, TEXT_FIELD_LENGTH, 10);
		textField.setValidator(NUMBER_FILTER);
		textField.setEnableBackgroundDrawing(false);
		IClimateState climateState = gui.container.getTargetedState();
		if(climateState.isPresent()) {
			textField.setText(Float.toString(climateState.get(type)));
		}else{
			textField.setVisible(false);
			textField.setEnabled(false);
		}
		table = new Table();
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(gui.textureFile);
		gui.drawTexturedModalRect(startX + textField.x - 2, startY + textField.y - 2, 196, 50, 52, 12);

		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		String title = Translator.translateToLocal("for.gui." + type.getName());
		fontRenderer.drawString(title, xPos + startX + width / 2 - fontRenderer.getStringWidth(title) / 2, startY + yPos + 2, ColourProperties.INSTANCE.get("gui.greenhouse." + type.getName() + ".header"));

		GlStateManager.color(1.0f, 1.0f, 1.0f);
		textField.drawTextBox();

		updateData();
		table.draw(xPos + startX - 2, yPos + startY + 26, 14737632, false);
	}

	private int updateTimer = 0;

	private void updateData(){
		if(updateTimer > 0){
			updateTimer--;
			return;
		}
		updateTimer = 40;
		table.clear();
		IClimateData data = gui.container.getData();
		for (Map.Entry<String, Float> entry : data.getData(type).entrySet()) {
			float value = entry.getValue();
			int percent = (int)(value * 100);
			if(percent != 0) {
				table.addValueEntry(entry.getKey(), percent + " %");
			}
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		boolean wasFocused = textField.isFocused();
		textField.mouseClicked(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop(), mouseButton);
		if (wasFocused && !textField.isFocused()) {
			gui.sendNetworkUpdate();
		}
	}

	public void setValue(float value) {
		textField.setText(Float.toString(value));
	}

	public float parseValue() {
		String text = textField.getText();
		if (text.isEmpty()) {
			return 2.0F;
		}
		try {
			float value = Float.parseFloat(text);
			return Math.max(0.0F, Math.min(value, 2.0F));
		} catch (NumberFormatException e) {
			return 2.0F;
		}
	}

	public boolean keyTyped(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_RETURN && textField.isFocused()) {
			textField.setFocused(false);
			gui.sendNetworkUpdate();
			return true;
		}
		return textField.textboxKeyTyped(typedChar, keyCode);
	}
}
