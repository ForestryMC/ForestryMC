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
package forestry.climatology.gui.elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.core.ForestryAPI;
import forestry.api.gui.events.GuiEvent;
import forestry.climatology.gui.GuiHabitatFormer;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Constants;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.StringUtil;

@SideOnly(Side.CLIENT)
public class HabitatSelectionElement extends ElementGroup {
	private static final Comparator<ClimateButton> BUTTON_COMPARATOR = Comparator.comparingDouble(ClimateButton::getComparingCode);
	private final List<ClimateButton> buttons = new ArrayList<>();
	private final IClimateTransformer transformer;

	public HabitatSelectionElement(int xPos, int yPos, IClimateTransformer transformer) {
		super(xPos, yPos, 60, 40);
		this.transformer = transformer;
		int x = 0;
		int y = 0;
		for (EnumClimate climate : EnumClimate.values()) {
			ClimateButton button = new ClimateButton(climate, x * 20, y * 20);
			buttons.add(button);
			add(button);
			x++;
			if (x >= 3) {
				y++;
				x = 0;
			}
		}
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		super.drawElement(mouseX, mouseY);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png"));
		Optional<ClimateButton> optional = buttons.stream().min(BUTTON_COMPARATOR);
		if (!optional.isPresent()) {
			return;
		}
		ClimateButton button = optional.get();
		drawTexturedModalRect(button.getX() - 1, button.getY() - 1, 0, 233, 22, 22);
	}

	private enum EnumClimate {
		ICY("habitats/snow", Biomes.ICE_PLAINS),
		COLD("habitats/taiga", Biomes.TAIGA),
		HILLS("habitats/hills", Biomes.SWAMPLAND),
		NORMAL("habitats/plains", Biomes.PLAINS),
		WARM("habitats/jungle", Biomes.JUNGLE),
		HOT("habitats/desert", Biomes.DESERT);
		private IClimateState climateState;
		private String spriteName;

		EnumClimate(String spriteName, Biome biome) {
			climateState = ClimateStateHelper.of(biome.getDefaultTemperature(), biome.getRainfall());
			this.spriteName = spriteName;
		}

		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite getSprite() {
			return ForestryAPI.textureManager.getDefault(spriteName);
		}
	}

	private class ClimateButton extends GuiElement {
		final EnumClimate climate;

		private ClimateButton(EnumClimate climate, int xPos, int yPos) {
			super(xPos, yPos, 20, 20);
			this.climate = climate;
			addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
				IClimateState climateState = climate.climateState;
				GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
				former.setClimate(climateState);
				former.sendClimateUpdate();
			});
			addTooltip((tooltip, element, mouseX, mouseY) -> {
				tooltip.add("T: " + StringUtil.floatAsPercent(climate.climateState.getTemperature()));
				tooltip.add("H: " + StringUtil.floatAsPercent(climate.climateState.getHumidity()));
			});
		}

		@Override
		public void drawElement(int mouseX, int mouseY) {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
			TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
			textureManager.bindTexture(new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png"));
			drawTexturedModalRect(0, 0, 204, 46, 20, 20);
			TextureManagerForestry.getInstance().bindGuiTextureMap();
			drawTexturedModalRect(2, 2, climate.getSprite(), 16, 16);
		}

		private double getComparingCode() {
			IClimateState target = transformer.getTarget();
			IClimateState state = climate.climateState;
			double temp = target.getTemperature() - state.getTemperature();
			double hem = target.getHumidity() - state.getHumidity();
			return Math.abs(temp + hem);
		}
	}
}
