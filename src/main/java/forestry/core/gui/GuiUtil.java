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
package forestry.core.gui;

import java.awt.Color;
import java.util.Collection;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.tooltips.ToolTipLine;

public class GuiUtil {
	public static void drawItemStack(GuiForestry gui, ItemStack stack, int xPos, int yPos) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		RenderHelper.enableGUIStandardItemLighting();

		RenderItem itemRender = GuiForestry.getItemRenderer();

		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		gui.setZLevel(100.0F);
		itemRender.zLevel = 100.0F;
		FontRenderer font = null;
		if (stack != null) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = gui.getFontRenderer();
		}
		itemRender.renderItemAndEffectIntoGUI(font, gui.mc.getTextureManager(), stack, xPos, yPos);
		itemRender.renderItemOverlayIntoGUI(font, gui.mc.getTextureManager(), stack, xPos, yPos);
		gui.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;

		RenderHelper.disableStandardItemLighting();
		GL11.glPopAttrib();
	}

	public static void drawToolTips(GuiForestry gui, ToolTip toolTips, int mouseX, int mouseY) {
		if (toolTips == null) {
			return;
		}
		if (toolTips.isEmpty()) {
			return;
		}

		RenderItem itemRender = GuiForestry.getItemRenderer();
		FontRenderer fontRendererObj = gui.getFontRenderer();

		int left = gui.getGuiLeft();
		int top = gui.getGuiTop();
		int length = 0;
		int height = 0;
		int x;
		int y;

		for (ToolTipLine tip : toolTips) {
			y = fontRendererObj.getStringWidth(tip.toString());

			height += 10 + tip.getSpacing();
			if (y > length) {
				length = y;
			}
		}

		x = mouseX - left + 12;
		y = mouseY - top - 12;

		gui.setZLevel(300.0F);
		itemRender.zLevel = 300.0F;
		Color backgroundColor = new Color(16, 0, 16, 240);
		int backgroundColorInt = backgroundColor.getRGB();
		gui.drawGradientRect(x - 3, y - 4, x + length + 2, y - 3, backgroundColorInt, backgroundColorInt);
		gui.drawGradientRect(x - 3, y + height + 1, x + length + 2, y + height + 2, backgroundColorInt, backgroundColorInt);
		gui.drawGradientRect(x - 3, y - 3, x + length + 2, y + height + 1, backgroundColorInt, backgroundColorInt);
		gui.drawGradientRect(x - 4, y - 3, x - 3, y + height + 1, backgroundColorInt, backgroundColorInt);
		gui.drawGradientRect(x + length + 2, y - 3, x + length + 3, y + height + 1, backgroundColorInt, backgroundColorInt);

		Color borderColorTop = new Color(80, 0, 255, 80);
		int borderColorTopInt = borderColorTop.getRGB();
		Color borderColorBottom = new Color((borderColorTopInt & 0xfefefe) >> 1 | borderColorTopInt & -0x1000000, true);
		int borderColorBottomInt = borderColorBottom.getRGB();
		gui.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height, borderColorTopInt, borderColorBottomInt);
		gui.drawGradientRect(x + length + 1, y - 3 + 1, x + length + 2, y + height, borderColorTopInt, borderColorBottomInt);
		gui.drawGradientRect(x - 3, y - 3, x + length + 2, y - 3 + 1, borderColorTopInt, borderColorTopInt);
		gui.drawGradientRect(x - 3, y + height, x + length + 2, y + height + 1, borderColorBottomInt, borderColorBottomInt);

		boolean firstLine = true;
		for (ToolTipLine tip : toolTips) {
			String line;

			if (firstLine) {
				line = tip.toString();
			} else {
				line = EnumChatFormatting.GRAY + tip.toString();
			}

			fontRendererObj.drawStringWithShadow(line, x, y, -1);

			y += 10 + tip.getSpacing();

			firstLine = false;
		}

		gui.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;
	}

	public static void drawToolTips(GuiForestry gui, Collection<?> objects, int mouseX, int mouseY) {
		for (Object obj : objects) {
			if (!(obj instanceof IToolTipProvider)) {
				continue;
			}
			IToolTipProvider provider = (IToolTipProvider) obj;
			if (!provider.isToolTipVisible()) {
				continue;
			}
			ToolTip tips = provider.getToolTip(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
			if (tips == null) {
				continue;
			}
			boolean mouseOver = provider.isMouseOver(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
			tips.onTick(mouseOver);
			if (mouseOver && tips.isReady()) {
				tips.refresh();
				drawToolTips(gui, tips, mouseX, mouseY);
			}
		}
	}
}
