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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.gui.events.GuiEvent;
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
		super(xPos, yPos, 52, 12);
		this.transformer = transformer;
		this.type = type;

		addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
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
		});
		addTooltip((tooltip, element, mouseX, mouseY) -> {
			IClimateState targetedState = transformer.getTarget();
			IClimateState state = transformer.getCurrent();
			IClimateState defaultState = transformer.getDefault();
			tooltip.add(new TranslationTextComponent("for.gui.habitat_former.climate.target", StringUtil.floatAsPercent(targetedState.getClimate(type))));
			tooltip.add(new TranslationTextComponent("for.gui.habitat_former.climate.value", StringUtil.floatAsPercent(state.getClimate(type))));
			tooltip.add(new TranslationTextComponent("for.gui.habitat_former.climate.default", StringUtil.floatAsPercent(defaultState.getClimate(type))));
		});
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		handleMouse(mouseX - getX(), mouseY - getY());

		GlStateManager.enableAlphaTest();
		GuiHabitatFormer gui = (GuiHabitatFormer) getWindow().getGui();
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bindTexture(gui.textureFile);

		setGLColorFromInt(type == ClimateType.TEMPERATURE ? 0xFFD700 : 0x7ff4f4);
		int progressScaled = getProgressScaled();
		blit(1, 1, 177, 69, progressScaled, 10);

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		blit(1 + getDefaultPosition(), 1, 232 + (type == ClimateType.TEMPERATURE ? 3 : 0), 69, 1, 10);
		blit(1 + getPointerPosition(), 1, 229, 69, 1, 10);
		blit(1, 1, 177, 80, 50, 10);
		GlStateManager.disableAlphaTest();
	}

	private int getProgressScaled() {
		float value = transformer.getCurrent().getClimate(type);
		return (int) (value * (width - 2) / MAX_VALUE);
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

		GlStateManager.color4f(red, green, blue, 1.0F);
	}

	private void handleMouse(int mouseX, int mouseY) {
		if (!dragging) {
			return;
		}
		if (mouseX < 1 || mouseY < 1 || mouseX > width - 1 || mouseY > height - 1) {
			return;
		}
		final float quotient = MathHelper.clamp((mouseX - 1) / (float) (width - 3), 0.0F, 1.0F);
		final float value = MAX_VALUE * quotient;
		GuiHabitatFormer former = (GuiHabitatFormer) getWindow().getGui();
		IClimateState climateState = former.getClimate();

		IClimateState newState = climateState.toImmutable().setClimate(type, value);
		former.setClimate(newState);
	}
}
