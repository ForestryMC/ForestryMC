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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.events.ElementEvent;
import forestry.climatology.gui.elements.ClimateBarElement;
import forestry.climatology.gui.elements.HabitatSelectionElement;
import forestry.climatology.gui.elements.SpeciesSelectionElement;
import forestry.climatology.network.packets.PacketSelectClimateTargeted;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.gui.elements.ScrollBarElement;
import forestry.core.gui.elements.TextEditElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
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
	private final ElementGroup selectionPage;
	private final ScrollBarElement rangeBar;
	//private final ElementGroup rangePage;
	//private final ButtonElement selectionButton;
	//private final ButtonElement rangeButton;
	private TextEditElement temperatureEdit;
	private TextEditElement humidityEdit;

	public GuiHabitatFormer(EntityPlayer player, TileHabitatFormer tile) {
		super(Constants.TEXTURE_PATH_GUI + "/habitat_former.png", new ContainerHabitatFormer(player.inventory, tile), tile);
		this.transformer = tile.getTransformer();
		this.tile = tile;
		this.ySize = 233;

		widgetManager.add(new TankWidget(widgetManager, 152, 17, 0));

		window.add(new ClimateBarElement(61, 33, transformer, ClimateType.TEMPERATURE));
		window.add(new ClimateBarElement(61, 57, transformer, ClimateType.HUMIDITY));

		rangeBar = window.add(new ScrollBarElement(10, 17, 12, 58, SCROLLBAR_SLIDER))
			.setParameters(this, 1, 16, 1);
		rangeBar.addTooltip((tooltip, element, mouseX, mouseY) -> {
			tooltip.add(Translator.translateToLocal("for.gui.habitat_former.climate.range"));
			tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("for.gui.habitat_former.climate.range.blocks", rangeBar.getValue()));
		});
		window.add(new CircleButton(30, 37));

		selectionPage = window.pane(8, 86, 164, 56);
		selectionPage.add(new SpeciesSelectionElement(135, 22, transformer));
		selectionPage.label(Translator.translateToLocal("for.gui.habitat_former.climate.habitats"), GuiElementAlignment.TOP_CENTER).setLocation(17, 3);
		selectionPage.add(new HabitatSelectionElement(67, 12, transformer));
		selectionPage.label(Translator.translateToLocal("for.gui.habitat_former.climate.temperature"), GuiElementAlignment.TOP_CENTER).setLocation(-49, 5);
		selectionPage.drawable(7, 15, TEMPERATURE_FIELD);
		temperatureEdit = selectionPage.add(new TextEditElement(9, 17, 50, 10).setMaxLength(3));
		temperatureEdit.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> setClimate(ClimateType.TEMPERATURE, temperatureEdit.getValue()));
		selectionPage.drawable(7, 39, HUMIDITY_FIELD);
		selectionPage.label(Translator.translateToLocal("for.gui.habitat_former.climate.humidity"), GuiElementAlignment.TOP_CENTER).setLocation(-49, 30);
		humidityEdit = selectionPage.add(new TextEditElement(9, 41, 50, 10).setMaxLength(3));
		humidityEdit.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> setClimate(ClimateType.HUMIDITY, humidityEdit.getValue()));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		IClimateState target = transformer.getTarget();
		if (humidityEdit != null && !window.isFocused(humidityEdit)) {
			humidityEdit.setValue(Integer.toString((int) (MathHelper.clamp(target.getHumidity(), 0.0F, 2.0F) * 100)));
		}
		if (temperatureEdit != null && !window.isFocused(temperatureEdit)) {
			temperatureEdit.setValue(Integer.toString((int) (MathHelper.clamp(target.getTemperature(), 0.0F, 2.0F) * 100)));
		}
		if (rangeBar.getValue() != transformer.getRange()) {
			updateRange();
		}
	}

	private void updateRange() {
		rangeBar.setValue(transformer.getRange());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		drawCenteredString(Translator.translateToLocal("for.gui.habitat_former.climate.temperature"), xSize / 2, 23);
		drawCenteredString(Translator.translateToLocal("for.gui.habitat_former.climate.humidity"), xSize / 2, 47);
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
			BlockPos pos = tile.getPos();
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
			super(xPos, yPos, 18, 18, CIRCLE_DISABLED_BUTTON, CIRCLE_ENABLED_BUTTON, button -> ((CircleButton) button).onButtonPressed());
			addTooltip((tooltip, element, mouseX, mouseY) -> tooltip.add(Translator.translateToLocal("for.gui.habitat_former.climate.circle." + (transformer.isCircular() ? "enabled" : "disabled"))));
		}

		private void onButtonPressed() {
			transformer.setCircular(!transformer.isCircular());
			NetworkUtil.sendToServer(new PacketGuiSelectRequest(ContainerHabitatFormer.REQUEST_ID_CIRCLE, transformer.isCircular() ? 1 : 0));
		}

		@Override
		protected int getHoverState(boolean mouseOver) {
			return transformer.isCircular() ? 1 : 0;
		}
	}
}
