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
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateState;
import forestry.api.core.ForestryAPI;
import forestry.core.climate.ClimateStates;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.TextureManagerForestry;
import forestry.greenhouse.gui.GuiGreenhouse;

public class WidgetClimateBar extends Widget {

	public static final int WIDTH = 90;
	public static final int HEIGHT = 23;

	private final List<ClimateButton> buttons = new ArrayList<>();

	public WidgetClimateBar(WidgetManager manager, int xPos, int yPos) {
		super(manager, xPos, yPos);
		this.width = WIDTH;
		this.height = HEIGHT;
		for (EnumClimate climate : EnumClimate.values()) {
			buttons.add(new ClimateButton(this, climate, xPos + 5 + climate.ordinal() * 16, yPos + 5));
		}
	}

	@Override
	public void draw(int startX, int startY) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 0, 202, width, height);
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
				IClimateState climateState = button.climate.climateState;
				((GuiGreenhouse) manager.gui).temperaturePanel.setValue(climateState.getTemperature());
				((GuiGreenhouse) manager.gui).humidityPanel.setValue(climateState.getHumidity());
				((GuiGreenhouse) manager.gui).sendNetworkUpdate();
			}
		}
	}

	@Override
	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		return isMouseOver(mouseX, mouseY);
	}

	protected void drawSprite(TextureAtlasSprite sprite, int x, int y) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
		TextureManagerForestry.getInstance().bindGuiTextureMap();
		manager.gui.drawTexturedModalRect(x, y, sprite, 16, 16);
	}

	private enum EnumClimate {
		ICY("habitats/snow", Biomes.ICE_PLAINS),
		COLD("habitats/taiga", Biomes.TAIGA),
		NORMAL("habitats/plains", Biomes.PLAINS),
		WARM("habitats/jungle", Biomes.JUNGLE),
		HOT("habitats/desert", Biomes.DESERT);
		IClimateState climateState;
		String spriteName;

		EnumClimate(String spriteName, Biome biome) {
			climateState = ClimateStates.of(biome.getDefaultTemperature(), biome.getRainfall());
			this.spriteName = spriteName;
		}

		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite getSprite() {
			return ForestryAPI.textureManager.getDefault(spriteName);
		}
	}

	private class ClimateButton {
		final WidgetClimateBar parent;
		final EnumClimate climate;
		protected final ToolTip toolTip = new ToolTip(250) {
			@Override
			@SideOnly(Side.CLIENT)
			public void refresh() {
				toolTip.clear();
				toolTip.add("T: " + climate.climateState.getTemperature());
				toolTip.add("H: " + climate.climateState.getHumidity());
			}
		};
		final int xPos, yPos;

		public ClimateButton(WidgetClimateBar parent, EnumClimate climate, int xPos, int yPos) {
			this.parent = parent;
			this.climate = climate;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		public void draw(int startX, int startY) {
			parent.drawSprite(climate.getSprite(), startX + xPos, startY + yPos);
		}

		public ToolTip getToolTip() {
			return toolTip;
		}

		public boolean isMouseOver(int mouseX, int mouseY) {
			return mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16;
		}
	}
}
