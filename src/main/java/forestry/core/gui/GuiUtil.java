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

import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

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
		itemRender.renderItemAndEffectIntoGUI(stack, xPos, yPos);
		itemRender.renderItemOverlayIntoGUI(font, stack, xPos, yPos, "");
		gui.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;

		RenderHelper.disableStandardItemLighting();
		GL11.glPopAttrib();
	}

	public static void drawToolTips(GuiForestry gui, ToolTip toolTips, int mouseX, int mouseY) {
		if (toolTips == null) {
			return;
		}

		List<String> lines = toolTips.getLines();
		if (!lines.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(-gui.getGuiLeft(), -gui.getGuiTop(), 0);
			ScaledResolution scaledresolution = new ScaledResolution(gui.mc);
			GuiUtils.drawHoveringText(lines, mouseX, mouseY, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -1, gui.mc.fontRendererObj);
			GlStateManager.popMatrix();
		}
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