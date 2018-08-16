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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateLogic;
import forestry.api.climate.IClimateState;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.ILabelElement;
import forestry.api.gui.events.ElementEvent;
import forestry.climatology.ClimatologyEventHandler;
import forestry.climatology.gui.elements.ClimateBarElement;
import forestry.climatology.gui.elements.HabitatSelectionElement;
import forestry.climatology.gui.elements.HabitatformerButton;
import forestry.climatology.gui.elements.SpeciesSelectionElement;
import forestry.climatology.gui.ledgers.HabitatformerLedger;
import forestry.climatology.network.packets.PacketSelectClimateTargeted;
import forestry.climatology.tiles.TileHabitatformer;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.gui.elements.ScrollBarElement;
import forestry.core.gui.elements.TextEditElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class GuiHabitatformer extends GuiForestryTitled<ContainerHabitatformer> implements IScrollable {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/habitat_former.png");
	//Drawables
	private static final Drawable TEMPERATURE_FIELD = new Drawable(TEXTURE, 204, 22, 52, 12);
	private static final Drawable HUMIDITY_FIELD = new Drawable(TEXTURE, 204, 34, 52, 12);
	private static final Drawable SCROLLBAR_BACKGROUND = new Drawable(TEXTURE, 22, 233, 154, 14);
	private static final Drawable SCROLLBAR_SLIDER = new Drawable(TEXTURE, 241, 68, 15, 12);
	private static final Drawable PREVIEW_ENABLED_BUTTON = new Drawable(TEXTURE, 238, 92, 18, 18);
	private static final Drawable PREVIEW_DISABLED_BUTTON = new Drawable(TEXTURE, 220, 92, 18, 18);
	private static final Drawable CIRCLE_ENABLED_BUTTON = new Drawable(TEXTURE, 238, 110, 18, 18);
	private static final Drawable CIRCLE_DISABLED_BUTTON = new Drawable(TEXTURE, 220, 110, 18, 18);

	private final TileHabitatformer tile;
	private final IClimateLogic logic;
	private final ElementGroup selectionPage;
	private final ElementGroup rangePage;
	private final ButtonElement selectionButton;
	private final ButtonElement rangeButton;
	@Nullable
	private TextEditElement temperatureEdit = null;
	@Nullable
	private TextEditElement humidityEdit = null;
	@Nullable
	private ILabelElement rangeLabel = null;
	@Nullable
	private ScrollBarElement rangeBar = null;

	public GuiHabitatformer(EntityPlayer player, TileHabitatformer tile) {
		super(Constants.TEXTURE_PATH_GUI + "/habitat_former.png", new ContainerHabitatformer(player.inventory, tile), tile);
		this.logic = tile.getLogic();
		this.tile = tile;
		this.ySize = 233;

		widgetManager.add(new TankWidget(widgetManager, 152, 17, 0));
		widgetManager.add(new SocketWidget(widgetManager, 19, 39, tile, 0));

		window.add(new ClimateBarElement(61, 33, logic, ClimateType.TEMPERATURE));
		window.add(new ClimateBarElement(61, 57, logic, ClimateType.HUMIDITY));

		this.selectionPage = createSelectionPage();
		this.rangePage = createRangePage();
		this.selectionButton = window.add(new HabitatformerButton(6, 64, true, this::onButtonClicked));
		this.rangeButton = window.add(new HabitatformerButton(30, 64, false, this::onButtonClicked));
		rangePage.hide();
		selectionButton.setEnabled(false);
		rangeButton.setEnabled(true);
	}

	private void onButtonClicked(boolean sectionButton) {
		if (sectionButton) {
			selectionPage.show();
			rangePage.hide();
			rangeButton.setEnabled(true);
			selectionButton.setEnabled(false);
		} else {
			selectionPage.hide();
			rangePage.show();
			rangeButton.setEnabled(false);
			selectionButton.setEnabled(true);
		}
	}

	private ElementGroup createSelectionPage() {
		ElementGroup page = window.pane(8, 86, 164, 56);
		page.add(new SpeciesSelectionElement(135, 22, logic));
		page.label(Translator.translateToLocal("for.gui.habitatformer.climate.habitats"), GuiElementAlignment.TOP_CENTER).setLocation(17, 3);
		page.add(new HabitatSelectionElement(67, 12, logic));
		page.label(Translator.translateToLocal("for.gui.habitatformer.climate.temperature"), GuiElementAlignment.TOP_CENTER).setLocation(-49, 5);
		page.drawable(7, 15, TEMPERATURE_FIELD);
		temperatureEdit = page.add(new TextEditElement(9, 17, 50, 10).setMaxLength(3));
		//temperatureEdit.addSelfEventHandler(TextEditEvent.class, event -> setClimate(ClimateType.TEMPERATURE, event.getNewValue()));
		temperatureEdit.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> setClimate(ClimateType.TEMPERATURE, temperatureEdit.getValue()));
		page.drawable(7, 39, HUMIDITY_FIELD);
		page.label(Translator.translateToLocal("for.gui.habitatformer.climate.humidity"), GuiElementAlignment.TOP_CENTER).setLocation(-49, 30);
		humidityEdit = page.add(new TextEditElement(9, 41, 50, 10).setMaxLength(3));
		//humidityEdit.addSelfEventHandler(TextEditEvent.class, event -> setClimate(ClimateType.HUMIDITY, event.getNewValue()));
		humidityEdit.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> setClimate(ClimateType.HUMIDITY, humidityEdit.getValue()));
		return page;
	}

	private ElementGroup createRangePage() {
		ElementGroup page = window.pane(8, 86, 164, 56);
		page.label(Translator.translateToLocal("for.gui.habitatformer.climate.range"), GuiElementAlignment.TOP_CENTER).setLocation(0, 2);
		rangeBar = page.add(new ScrollBarElement(3, 36, SCROLLBAR_BACKGROUND, true, SCROLLBAR_SLIDER)
			.setVertical()
			.setParameters(this, 1, 16, 1));
		rangeLabel = page.label("", GuiElementAlignment.TOP_CENTER);
		rangeLabel.setYPosition(26);
		updateRange();
		page.add(new CircleButton(3, 9));
		page.add(new PreviewButton(-8, 9).setAlign(GuiElementAlignment.TOP_RIGHT));
		return page;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		IClimateState target = logic.getTarget();
		if (humidityEdit != null && !window.isFocused(humidityEdit)) {
			humidityEdit.setValue(Integer.toString((int) (MathHelper.clamp(target.getHumidity(), 0.0F, 2.0F) * 100)));
		}
		if (temperatureEdit != null && !window.isFocused(temperatureEdit)) {
			temperatureEdit.setValue(Integer.toString((int) (MathHelper.clamp(target.getTemperature(), 0.0F, 2.0F) * 100)));
		}
		if (rangeLabel != null && rangeBar != null && rangeBar.getValue() != logic.getRange()) {
			updateRange();
		}
	}

	private void updateRange() {
		if (rangeLabel != null && rangeBar != null) {
			rangeLabel.setText(Translator.translateToLocalFormatted("for.gui.habitatformer.climate.range.blocks", logic.getRange()));
			rangeBar.setValue(logic.getRange());
			ClimatologyEventHandler.updateDebugPositions(tile.getCoordinates(), tile.getLogic().getRange(), tile.getLogic().isCircular());
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		drawCenteredString(Translator.translateToLocal("for.gui.habitatformer.climate.temperature"), xSize / 2, 23);
		drawCenteredString(Translator.translateToLocal("for.gui.habitatformer.climate.humidity"), xSize / 2, 47);
	}

	private void drawCenteredString(String text, int x, int y) {
		fontRenderer.drawStringWithShadow(text, guiLeft + (float) (x - (double) fontRenderer.getStringWidth(text) / 2), (float) guiTop + y, 16777215);
	}

	public void setClimate(ClimateType type, String text) {
		int value;
		try {
			value = Integer.parseInt(text);
		} catch (NumberFormatException exception) {
			value = 0;
		}
		float climateValue = MathHelper.clamp(((float) value / 100.0F), 0.0F, 2.0F);
		IClimateState target = logic.getTarget();
		setClimate(target.setClimate(type, climateValue));
		sendClimateUpdate();
	}

	public void setClimate(IClimateState state) {
		logic.setTarget(state.copy());
	}

	public void sendClimateUpdate() {
		IClimateState targetedState = logic.getTarget();
		if (targetedState.isPresent()) {
			BlockPos pos = tile.getPos();
			NetworkUtil.sendToServer(new PacketSelectClimateTargeted(pos, targetedState));
		}
	}

	public IClimateState getClimate() {
		return logic.getTarget();
	}

	@Override
	protected void addLedgers() {
		addClimateLedger(tile);
		addErrorLedger(tile);
		addPowerLedger(tile.getEnergyManager());
		addHintLedger("habitatformer");
		ledgerManager.add(new HabitatformerLedger(ledgerManager, tile.getLogic()));
	}

	@Override
	public void onScroll(int value) {
		NetworkUtil.sendToServer(new PacketGuiSelectRequest(ContainerHabitatformer.REQUEST_ID_RANGE, value));
		if (rangeLabel != null) {
			rangeLabel.setText(Translator.translateToLocalFormatted("for.gui.habitatformer.climate.range.blocks", value));
			logic.setRange(value);
			ClimatologyEventHandler.updateDebugPositions(tile.getCoordinates(), tile.getLogic().getRange(), tile.getLogic().isCircular());
		}
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return rangePage.isMouseOver();
	}

	private class CircleButton extends ButtonElement {

		private CircleButton(int xPos, int yPos) {
			super(xPos, yPos, 18, 18, CIRCLE_DISABLED_BUTTON, CIRCLE_ENABLED_BUTTON, button -> ((CircleButton) button).onButtonPressed());
		}

		private void onButtonPressed() {
			logic.setCircular(!logic.isCircular());
			ClimatologyEventHandler.updateDebugPositions(tile.getCoordinates(), tile.getLogic().getRange(), tile.getLogic().isCircular());
			NetworkUtil.sendToServer(new PacketGuiSelectRequest(ContainerHabitatformer.REQUEST_ID_CIRCLE, logic.isCircular() ? 1 : 0));
		}

		@Override
		protected int getHoverState(boolean mouseOver) {
			return logic.isCircular() ? 1 : 0;
		}

		@Override
		public boolean hasTooltip() {
			return true;
		}

		@Override
		public List<String> getTooltip(int mouseX, int mouseY) {
			List<String> tooltip = new ArrayList<>(getTooltip());
			tooltip.add(Translator.translateToLocal("for.gui.habitatformer.climate.circle." + (logic.isCircular() ? "enabled" : "disabled")));
			return tooltip;
		}
	}

	private class PreviewButton extends ButtonElement {

		private PreviewButton(int xPos, int yPos) {
			super(xPos, yPos, 18, 18, PREVIEW_DISABLED_BUTTON, PREVIEW_ENABLED_BUTTON, button -> ((PreviewButton) button).onButtonPressed());
		}

		private void onButtonPressed() {
			if (ClimatologyEventHandler.getCurrentFormer() != tile.getCoordinates()) {
				ClimatologyEventHandler.setDebugPositions(tile.getCoordinates(), tile.getLogic().getRange(), tile.getLogic().isCircular());
			} else {
				ClimatologyEventHandler.clearDebugPositions();
			}
		}

		@Override
		protected int getHoverState(boolean mouseOver) {
			return ClimatologyEventHandler.getCurrentFormer() == tile.getCoordinates() ? 1 : 0;
		}

		@Override
		public boolean hasTooltip() {
			return true;
		}

		@Override
		public List<String> getTooltip(int mouseX, int mouseY) {
			List<String> tooltip = new ArrayList<>(getTooltip());
			tooltip.add(Translator.translateToLocal("for.gui.habitatformer.climate.preview." + (logic.isCircular() ? "enabled" : "disabled")));
			return tooltip;
		}
	}
}
