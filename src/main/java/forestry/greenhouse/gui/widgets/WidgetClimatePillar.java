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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.TextureManagerForestry;
import forestry.greenhouse.gui.GuiGreenhouse;

public class WidgetClimatePillar extends Widget {

	public static final int WIDTH = 23;
	public static final int HEIGHT = 90;

	private final List<ClimateButton> buttons = new ArrayList<>();

	public WidgetClimatePillar(WidgetManager manager, int xPos, int yPos) {
		super(manager, xPos, yPos);
		this.width = WIDTH;
		this.height = HEIGHT;
		for (int i = 1; i < 6; i++) {
			EnumTemperature temp = EnumTemperature.VALUES[i];
			float value;
			switch (temp) {
				case ICY:
					value = 0.0F;
					break;
				case COLD:
					value = 0.2F;
					break;
				case WARM:
					value = 0.9F;
					break;
				case HOT:
					value = 1.2F;
					break;
				default:
					value = 0.5F;
					break;
			}
			buttons.add(new ClimateButton(this, temp, value, xPos + 5, yPos + 5 + (i - 1) * 16));
		}
	}

	@Override
	public void draw(int startX, int startY) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 220, 25, width, height);
		for (ClimateButton button : buttons) {
			button.draw(startX, startY);
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		for (ClimateButton button : buttons) {
			if (button.isMouseOver(mouseX, mouseY)) {
				return button.getToolTip();
			}
		}
		return null;
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		mouseX -= manager.gui.getGuiLeft();
		mouseY -= manager.gui.getGuiTop();
		for (ClimateButton button : buttons) {
			if (button.isMouseOver(mouseX, mouseY)) {
				((GuiGreenhouse) manager.gui).temperaturePanel.setValue(button.value);
			}
		}
	}

	@Override
	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		mouseX -= manager.gui.getGuiLeft();
		mouseY -= manager.gui.getGuiTop();
		return isMouseOver(mouseX, mouseY);
	}

	protected void drawSprite(TextureAtlasSprite sprite, int x, int y) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
		TextureManagerForestry.getInstance().bindGuiTextureMap();
		manager.gui.drawTexturedModalRect(x, y, sprite, 16, 16);
	}

	private static class ClimateButton {
		final WidgetClimatePillar parent;
		final EnumTemperature temperature;
		final float value;
		protected final ToolTip toolTip = new ToolTip(250) {
			@Override
			@SideOnly(Side.CLIENT)
			public void refresh() {
				toolTip.clear();
				toolTip.add("T: " + AlleleManager.climateHelper.toDisplay(temperature));
				toolTip.add("V: " + value);
			}
		};
		final int xPos, yPos;

		public ClimateButton(WidgetClimatePillar parent, EnumTemperature temperature, float value, int xPos, int yPos) {
			this.parent = parent;
			this.temperature = temperature;
			this.value = value;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		public void draw(int startX, int startY) {
			parent.drawSprite(temperature.getSprite(), startX + xPos, startY + yPos);
		}

		public ToolTip getToolTip() {
			return toolTip;
		}

		public boolean isMouseOver(int mouseX, int mouseY) {
			return mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16;
		}
	}
}
