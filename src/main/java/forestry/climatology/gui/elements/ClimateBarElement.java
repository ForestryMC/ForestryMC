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

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.climatology.gui.GuiHabitatFormer;
import forestry.core.gui.elements.GuiElement;
import forestry.core.utils.StringUtil;

@OnlyIn(Dist.CLIENT)
public class ClimateBarElement extends GuiElement {
	public static final float MAX_VALUE = 2.0F;

	private final IClimateTransformer transformer;
	private final ClimateType type;
	private boolean dragging;

	public ClimateBarElement(int xPos, int yPos, IClimateTransformer transformer, ClimateType type) {
		super(xPos, yPos);
		setSize(52, 12);
		this.transformer = transformer;
		this.type = type;

		/*addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			if (Screen.hasControlDown()) {
				GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
				IClimateState climateState = former.getClimate();
				IClimateState newState = climateState.toImmutable().setClimate(type, transformer.getDefault().getTemperature());
				former.setClimate(newState);
				former.sendClimateUpdate();
				return;
			}
			dragging = true;
			handleMouse(event.getRelativeX(), event.getRelativeY());
		});
		addSelfEventHandler(GuiEvent.UpEvent.class, event -> {
			if (dragging) {
				dragging = false;
				GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
				former.sendClimateUpdate();
			}
		});*/
		addTooltip((tooltip, element, mouseX, mouseY) -> {
			IClimateState targetedState = transformer.getTarget();
			IClimateState state = transformer.getCurrent();
			IClimateState defaultState = transformer.getDefault();
			tooltip.add(Component.translatable("for.gui.habitat_former.climate.target", StringUtil.floatAsPercent(targetedState.getClimate(type))));
			tooltip.add(Component.translatable("for.gui.habitat_former.climate.value", StringUtil.floatAsPercent(state.getClimate(type))));
			tooltip.add(Component.translatable("for.gui.habitat_former.climate.default", StringUtil.floatAsPercent(defaultState.getClimate(type))));
		});
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (Screen.hasControlDown()) {
			GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
			IClimateState climateState = former.getClimate();
			IClimateState newState = climateState.toImmutable().setClimate(type, transformer.getDefault().getTemperature());
			former.setClimate(newState);
			former.sendClimateUpdate();
			return true;
		}
		dragging = true;
		return handleMouse((int) mouseX, (int) mouseY);
	}

	@Override
	public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (!dragging) {
			return false;
		}
		dragging = false;
		GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
		former.sendClimateUpdate();
		return true;
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		handleMouse(mouseY - getX(), mouseX - getY());

		// RenderSystem.enableAlphaTest();
		GuiHabitatFormer gui = (GuiHabitatFormer) getWindow().getGui();
		RenderSystem.setShaderTexture(0, gui.textureFile);

		setGLColorFromInt(type == ClimateType.TEMPERATURE ? 0xFFD700 : 0x7ff4f4);
		int progressScaled = getProgressScaled();
		blit(transform, 1, 1, 177, 69, progressScaled, 10);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		blit(transform, 1 + getDefaultPosition(), 1, 232 + (type == ClimateType.TEMPERATURE ? 3 : 0), 69, 1, 10);
		blit(transform, 1 + getPointerPosition(), 1, 229, 69, 1, 10);
		blit(transform, 1, 1, 177, 80, 50, 10);
		// RenderSystem.disableAlphaTest();
	}

	private int getProgressScaled() {
		float value = transformer.getCurrent().getClimate(type);
		return (int) (value * (preferredSize.width - 2) / MAX_VALUE);
	}

	private int getPointerPosition() {
		float targetedValue = transformer.getTarget().getClimate(type);
		return (int) (targetedValue * 49 / MAX_VALUE);
	}

	private int getDefaultPosition() {
		float value = transformer.getDefault().getClimate(type);
		return (int) (value * 49 / MAX_VALUE);
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		RenderSystem.setShaderColor(red, green, blue, 1.0F);
	}

	private boolean handleMouse(int mouseX, int mouseY) {
		if (!dragging) {
			return false;
		}
		if (mouseX < 1 || mouseY < 1 || mouseX > preferredSize.width - 1 || mouseY > preferredSize.height - 1) {
			return false;
		}
		final float quotient = Mth.clamp((mouseX - 1) / (float) (preferredSize.width - 3), 0.0F, 1.0F);
		final float value = MAX_VALUE * quotient;
		GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
		IClimateState climateState = former.getClimate();

		IClimateState newState = climateState.toImmutable().setClimate(type, value);
		former.setClimate(newState);
		return true;
	}
}
