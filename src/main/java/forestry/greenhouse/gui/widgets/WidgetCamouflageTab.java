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

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.greenhouse.multiblock.GreenhouseController;

public class WidgetCamouflageTab extends Widget {
	public static final int WIDTH = 68;
	public static final int HEIGHT = 25;

	private final WidgetCamouflageSlot greenhouseSlot;
	@Nullable
	private final WidgetCamouflageSlot handlerSlot;
	private final ItemStack typeStack;

	public WidgetCamouflageTab(WidgetManager manager, int xPos, int yPos, IGreenhouseController controller, ICamouflageHandler camouflageHandler) {
		super(manager, xPos, yPos);

		this.width = WIDTH;
		this.height = HEIGHT;
		greenhouseSlot = new WidgetCamouflageSlot(manager, xPos + 26, yPos + 6, controller);
		handlerSlot = new WidgetCamouflageSlot(manager, xPos + 46, yPos + 6, camouflageHandler);
		typeStack = GreenhouseController.createDefaultCamouflageBlock();
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft minecraft = Minecraft.getMinecraft();
		TextureManager textureManager = minecraft.getTextureManager();
		textureManager.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 196, 0, 48, 25);
		if (handlerSlot != null) {
			manager.gui.drawTexturedModalRect(startX + xPos + 44, startY + yPos, 196, 25, 24, 25);
		}
		if (!typeStack.isEmpty()) {
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderItem renderItem = minecraft.getRenderItem();
			renderItem.renderItemIntoGUI(typeStack, startX + xPos + 6, startY + yPos + 6);
		}
		greenhouseSlot.draw(startX, startY);
		if (handlerSlot != null) {
			handlerSlot.draw(startX, startY);
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		if (greenhouseSlot.isMouseOver(mouseX, mouseY)) {
			return greenhouseSlot.getToolTip(mouseX, mouseY);
		} else if (handlerSlot != null && handlerSlot.isMouseOver(mouseX, mouseY)) {
			return handlerSlot.getToolTip(mouseX, mouseY);
		} else {
			return null;
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		mouseX -= manager.gui.getGuiLeft();
		mouseY -= manager.gui.getGuiTop();
		if (greenhouseSlot.isMouseOver(mouseX, mouseY)) {
			greenhouseSlot.handleMouseClick(mouseX, mouseY, mouseButton);
		} else if (handlerSlot != null && handlerSlot.isMouseOver(mouseX, mouseY)) {
			handlerSlot.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		return isMouseOver(mouseX, mouseY);
	}

	@Nullable
	public WidgetCamouflageSlot getHandlerSlot() {
		return handlerSlot;
	}
}
