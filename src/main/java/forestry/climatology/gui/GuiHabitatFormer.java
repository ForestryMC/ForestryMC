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
package forestry.climatology.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.climatology.gui.elements.ClimateBarElement;
import forestry.climatology.gui.elements.HabitatSelectionElement;
import forestry.climatology.gui.elements.SpeciesSelectionElement;
import forestry.climatology.network.packets.PacketSelectClimateTargeted;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.gui.elements.ScrollBarElement;
import forestry.core.gui.elements.TextEditElement;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

@OnlyIn(Dist.CLIENT)
public class GuiHabitatFormer extends GuiForestryTitled<ContainerHabitatFormer> implements IScrollable {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png");
	//Drawables
	private static final Drawable TEMPERATURE_FIELD = new Drawable(TEXTURE, 204, 22, 52, 12);
	private static final Drawable HUMIDITY_FIELD = new Drawable(TEXTURE, 204, 34, 52, 12);
	private static final Drawable SCROLLBAR_SLIDER = new Drawable(TEXTURE, 176, 92, 12, 15);
	private static final Drawable CIRCLE_ENABLED_BUTTON = new Drawable(TEXTURE, 238, 110, 18, 18);
	private static final Drawable CIRCLE_DISABLED_BUTTON = new Drawable(TEXTURE, 220, 110, 18, 18);

	private final TileHabitatFormer tile;
	private final IClimateTransformer transformer;
	private final ScrollBarElement rangeBar;
	//private final ElementGroup rangePage;
	//private final ButtonElement selectionButton;
	//private final ButtonElement rangeButton;
	private final TextEditElement temperatureEdit;
	private final TextEditElement humidityEdit;

	//TODO: Fix field focus
	public GuiHabitatFormer(ContainerHabitatFormer container, Inventory inv, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/habitat_former.png", container, inv, title);
		this.tile = container.getTile();
		this.transformer = tile.getTransformer();
		this.imageHeight = 233;

		widgetManager.add(new TankWidget(widgetManager, 152, 17, 0));

		window.add(new ClimateBarElement(61, 33, transformer, ClimateType.TEMPERATURE));
		window.add(new ClimateBarElement(61, 57, transformer, ClimateType.HUMIDITY));

		rangeBar = window.add(new ScrollBarElement(SCROLLBAR_SLIDER))
				.setParameters(this, 1, 16, 1);
		rangeBar.setPreferredBounds(10, 17, 12, 58);
		rangeBar.addTooltip((tooltip, element, mouseX, mouseY) -> {
			tooltip.add(Component.translatable("for.gui.habitat_former.climate.range"));
			tooltip.add(Component.translatable("for.gui.habitat_former.climate.range.blocks", rangeBar.getValue()).withStyle(ChatFormatting.GRAY));
		});
		window.add(new CircleButton(30, 37));

		ContainerElement selectionPage = window.pane(8, 86, 164, 56);
		selectionPage.add(new SpeciesSelectionElement(135, 22, transformer));
		selectionPage.translated("for.gui.habitat_former.climate.habitats").setAlign(Alignment.TOP_CENTER).setLocation(17, 3);
		selectionPage.add(new HabitatSelectionElement(67, 12, transformer));
		selectionPage.translated("for.gui.habitat_former.climate.temperature").setAlign(Alignment.TOP_CENTER).setLocation(-49, 5);
		selectionPage.drawable(7, 15, TEMPERATURE_FIELD);
		temperatureEdit = selectionPage.add(new TextEditElement(3));
		temperatureEdit.setPos(9, 17).setSize(50, 10);
		//temperatureEdit.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> setClimate(ClimateType.TEMPERATURE, temperatureEdit.getValue()));
		selectionPage.drawable(7, 39, HUMIDITY_FIELD);
		selectionPage.translated("for.gui.habitat_former.climate.humidity").setAlign(Alignment.TOP_CENTER).setLocation(-49, 30);
		humidityEdit = selectionPage.add(new TextEditElement(3));
		humidityEdit.setPos(9, 41).setSize(50, 10);
		//humidityEdit.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> setClimate(ClimateType.HUMIDITY, humidityEdit.getValue()));
	}

	@Override
	public void containerTick() {
		super.containerTick();
		IClimateState target = transformer.getTarget();
		if (!window.isFocused(humidityEdit)) {
			humidityEdit.setValue(Integer.toString((int) (Mth.clamp(target.getHumidity(), 0.0F, 2.0F) * 100)));
		}
		if (!window.isFocused(temperatureEdit)) {
			temperatureEdit.setValue(Integer.toString((int) (Mth.clamp(target.getTemperature(), 0.0F, 2.0F) * 100)));
		}
		if (rangeBar.getValue() != transformer.getRange()) {
			updateRange();
		}
	}

	private void updateRange() {
		rangeBar.setValue(transformer.getRange());
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(transform, partialTicks, mouseX, mouseY);
		drawCenteredString(transform, Translator.translateToLocal("for.gui.habitat_former.climate.temperature"), imageWidth / 2, 23);
		drawCenteredString(transform, Translator.translateToLocal("for.gui.habitat_former.climate.humidity"), imageWidth / 2, 47);
	}

	private void drawCenteredString(PoseStack transform, String text, int x, int y) {
		minecraft.font.drawShadow(transform, text, leftPos + (float) (x - (double) minecraft.font.width(text) / 2), (float) topPos + y, 16777215);
	}

	public void setClimate(ClimateType type, String text) {
		int value;
		try {
			value = Integer.parseInt(text);
		} catch (NumberFormatException exception) {
			value = 0;
		}
		float climateValue = Mth.clamp(((float) value / 100.0F), 0.0F, 2.0F);
		IClimateState target = transformer.getTarget();
		setClimate(target.setClimate(type, climateValue));
		sendClimateUpdate();
	}

	public void setClimate(IClimateState state) {
		transformer.setTarget(state.copy());
	}

	public void sendClimateUpdate() {
		IClimateState targetedState = transformer.getTarget();
		if (targetedState.isPresent()) {
			BlockPos pos = tile.getBlockPos();
			NetworkUtil.sendToServer(new PacketSelectClimateTargeted(pos, targetedState));
		}
	}

	public IClimateState getClimate() {
		return transformer.getTarget();
	}

	@Override
	protected void addLedgers() {
		addClimateLedger(tile);
		addErrorLedger(tile);
		addPowerLedger(tile.getEnergyManager());
		addHintLedger("habitat_former");
	}

	@Override
	public void onScroll(int value) {
		NetworkUtil.sendToServer(new PacketGuiSelectRequest(ContainerHabitatFormer.REQUEST_ID_RANGE, value));
		transformer.setRange(value);
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return rangeBar.isMouseOver();
	}

	private class CircleButton extends ButtonElement {

		private CircleButton(int xPos, int yPos) {
			super(new ButtonElement.Builder()
					.size(18)
					.textures(CIRCLE_DISABLED_BUTTON, CIRCLE_ENABLED_BUTTON)
					.action(button -> ((CircleButton) button).onButtonPressed())
					.pos(xPos, yPos)
			);
			addTooltip((tooltip, element, mouseX, mouseY) -> tooltip.add(Component.translatable("for.gui.habitat_former.climate.circle." + (transformer.isCircular() ? "enabled" : "disabled"))));
		}

		private void onButtonPressed() {
			transformer.setCircular(!transformer.isCircular());
			NetworkUtil.sendToServer(new PacketGuiSelectRequest(ContainerHabitatFormer.REQUEST_ID_CIRCLE, transformer.isCircular() ? 1 : 0));
		}

		@Override
		protected int getTextureIndex(boolean mouseOver) {
			return transformer.isCircular() ? 1 : 0;
		}
	}
}
